package com.hyperiongray.rcmp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspose.pdf.Rectangle;
import com.aspose.pdf.TextExtractionOptions;
import com.google.common.io.Files;
import com.hyperiongray.rcmp.extract.DataKey;
import com.hyperiongray.rcmp.extract.MarkerBasedExtractor;
import com.hyperiongray.rcmp.extract.MarkerBasedExtractor.Type;
import com.hyperiongray.rcmp.extract.RegexpExtractor;

/**
 *
 * @author mark
 */
public class ReportExtractor {

    private final static Logger logger = LoggerFactory.getLogger(ReportExtractor.class);

    private final Tika tika = new Tika();
    
    private final String[] markers1 = {
        "Report no.:",
        "Occurrence Type:",
        "Occurrence time:",
        "Reported time:",
        "Place of offence:",
        "Clearance status:",
        "Concluded:",
        "Concluded date:",
        "Summary:",
        "Remarks:",
        "Associated occurrences:",
        "Involved persons:",
        "Involved addresses:",
        "Involved comm addresses:",
        "Involved vehicles:",
        "Involved officers:",
        "Involved property:",
        "Modus operandi:",
        "Reports:",
        "Supplementary report:"
    };
    private static final String SUMMARY = "Summary:";
    private static final String INVOLVED_PERSONS = "Involved persons:";
    private static final String INVOLVED_ADDRESSES = "Involved addresses:";
    private static final String CASE_NUMBER = "Report no.:";
    private KeyEntry currentKeyEntry;
    private String currentCaseNumber;

    private final String[] markers2 = {
        "TICKET   NO:",
        "LAST NAME:",
        "FIRST NAME:",
        "SEX:",
        "DRIVER'S LICENCE NO.:",
        "DRIVER'S LICENCE PROV:",
        "DOB:",
        "ADDRESS:",
        "DESCRIPTION OF OFFENCE:"
    };

    private final String[] markers3 = {
        "Person name",
        "Ticket number",
        "Unique key                        "}; // spaces for Excel cell formatting

    private String outputFile;
    private String inputDir;
    // not lazy initialization, to avoid threading problems
    private static final ReportExtractor instance = new ReportExtractor();
    private int docCount;
    private final static String separator = "|";

	public static final String NEW_LINE = "#newline";
	public static final String PARAGRAPH = "#paragraph";

    public static ReportExtractor getInstance() {
        return instance;
    }

    private ReportExtractor() {
        // singleton
        initAsposeLicense();
    }

    public void doConvert() throws IOException {
        logger.info("Preparing to convert here: {}, output results there: {}", inputDir, outputFile);
        initializeOutputFiles();
        // for each report, add extracted information to the appropriate target files
        File[] files = new File(getInputDir()).listFiles();
        for (File file : files) {
            try {
                if (file.isFile() && file.exists()) {
                    String fileName = file.getName();
                    if (fileName.length() > 4 && ".pdf".equalsIgnoreCase(fileName.substring(fileName.length() - 4))) {
                        ExtractedData data = extractInfo(file);
                        saveData(data);
                        ++docCount;
                    }
                }
            } catch (IOException | TikaException e) {
                logger.error("Problem converting file {}", file.getName(), e);
            }
        }
        writeKeyFile();
    }

    private void initializeOutputFiles() throws IOException {
        new File(getOutputFile1()).delete();
        Files.append(flatten(markers1, separator), new File(getOutputFile1()), Charset.defaultCharset());
        new File(getOutputFile2()).delete();
        Files.append(flatten(markers2, separator), new File(getOutputFile2()), Charset.defaultCharset());
        logger.info("Will output into two files: {} and {}", getOutputFile1(), getOutputFile2());
    }

    private void writeKeyFile() throws IOException {
        logger.info("Writing the key file: {}", getOutputKeyFile());
        new File(getOutputKeyFile()).delete();
        Files.append(flatten(markers3, separator), new File(getOutputKeyFile()), Charset.defaultCharset());
        Map<String, KeyEntry> keyTable = KeyTable.getInstance().getKeyTable();
        Iterator<String> iter = keyTable.keySet().iterator();
        String[] values = new String[3];
        while (iter.hasNext()) {
            KeyEntry entry = keyTable.get(iter.next());
            values[0] = entry.getPersonName();
            values[1] = entry.getTicketNumber();
            values[2] = entry.getHashKey();
            Files.append(flatten((String[]) values, separator), new File(getOutputKeyFile()), Charset.defaultCharset());
        }
    }

    public ExtractedData extractInfo(File file) throws IOException, TikaException {
        //String pdfText = extractWithTika(file);
        //String pdfText = extractWithPdfBox(file);
        currentCaseNumber = "";
        currentKeyEntry = null;
        String pdfText = extractWithAspose(file);
        int fileType = determineFileType(pdfText);
        ExtractedData extractedData;
        if (fileType == 1) {
        	extractedData = extractDataType1(pdfText);
        } else if (fileType == 2) {
        	List<String> tokens = extractTokensWithAspose(file);
        	extractedData = extractDataType2(tokens);
        } else {
        	throw new IllegalStateException("Unknown file type " + fileType);
        }
//      String pdfText = extractWithAspose(file);
        logger.debug("File: {}", file.getPath());
        logger.trace(pdfText);
        return extractedData;
    }

    private ExtractedData extractDataType1(String pdfText) throws IOException {
        List<String> values = new ArrayList<>();
        String[] markers = markers1;
        Map<String, String> data = new HashMap<String, String>();
        for (int m = 0; m < markers.length; ++m) {
            String marker = markers[m];
            String value = "";
            int markerStart = pdfText.indexOf(marker);
            if (markerStart >= 0) {
                if (m < markers.length - 1) {
                    String nextMarker = markers[m + 1];
                    int markerStartNext = pdfText.indexOf(nextMarker);
                    if (markerStartNext > 0) {
                        String betweenTheMarkers = pdfText.substring(markerStart + marker.length(), markerStartNext).trim();
                        logger.debug("Marker: {}", marker);
                        logger.debug("Following text: {}", betweenTheMarkers);
                        value = sanitize(marker, betweenTheMarkers);
                    }
                }
            }
            values.add(value);
        }
        // form privacy entry
        if (currentKeyEntry != null) {
            KeyTable.getInstance().put(currentKeyEntry);
        }
        return new ExtractedData(1, data);
    }

    private ExtractedData extractDataType2(List<String> tokens) {
    	Map<String, String> ret = new HashMap<String, String>();
    	String text = Utils.join(tokens);

    	ret.put(DataKey.TICKET_NO.fieldName(), new MarkerBasedExtractor("TICKET NO:").extract(text, Type.NEXT_WORD));
    	ret.put(DataKey.LAST_NAME.fieldName(), new MarkerBasedExtractor("LAST NAME:").extract(text, Type.LINE));
    	ret.put(DataKey.FIRST_NAME.fieldName(), new MarkerBasedExtractor("FIRST NAME:").extract(text, Type.LINE));
    	ret.put(DataKey.SEX.fieldName(), new MarkerBasedExtractor("SEX:").extract(text, Type.NEXT_WORD));
    	ret.put(DataKey.DRIVERS_LICENCE_NO.fieldName(), new MarkerBasedExtractor("DRIVER'S LICENCE NO.:").extract(text, Type.LINE));
    	ret.put(DataKey.DRIVERS_LICENCE_PROV.fieldName(), new MarkerBasedExtractor("DRIVER'S LICENCE PROV:").extract(text, Type.LINE));
    	ret.put(DataKey.DRIVERS_LICENCE_PROV.fieldName(), new MarkerBasedExtractor("DRIVER'S LICENCE PROV:").extract(text, Type.LINE));
    	ret.put(DataKey.VEHICLE_MAKE.fieldName(), new MarkerBasedExtractor("Make:").extract(text, Type.LINE));
    	ret.put(DataKey.VEHICLE_MODEL.fieldName(), new MarkerBasedExtractor("Model:").extract(text, Type.LINE));
    	ret.put(DataKey.VEHICLE_YEAR.fieldName(), new MarkerBasedExtractor("Year:").extract(text, Type.NEXT_WORD));
    	ret.put(DataKey.VEHICLE_LICENCE_NO.fieldName(), new MarkerBasedExtractor("Licence No.:").extract(text, Type.NEXT_WORD));
    	ret.put(DataKey.VEHICLE_PROVINCE.fieldName(), new MarkerBasedExtractor("Province:").extract(text, Type.LINE));
    	ret.put(DataKey.VEHICLE_OWNERS_NAME.fieldName(), new MarkerBasedExtractor("Owner's Name:").extract(text, Type.LINE));
    	ret.put(DataKey.VEHICLE_OWNER_ADDRESS.fieldName(), new MarkerBasedExtractor("Address:").extract(text, Type.LINE));
    	ret.put(DataKey.VEHICLE_EXP_YEAR.fieldName(), new MarkerBasedExtractor("Exp. Year:").extract(text, Type.NEXT_WORD));
    	
    	ret.put(DataKey.CLOCKED_SPEED.fieldName(), new RegexpExtractor("Vehicle was clocked at (.*?) km").extract(text));
    	ret.put(DataKey.SPEED_LIMIT_EXCEEDED.fieldName(), new RegexpExtractor("Exceed Speed Limit of (.*?) km").extract(text));   
    	ret.put(DataKey.DATE.fieldName(), new RegexpExtractor("On ((.){0,20} at [0-9:]{0,10}+ (AM|PM)?)").extract(text));
    	ret.put(DataKey.PAYMENT_OPTION.fieldName(), new MarkerBasedExtractor("A payment option of").extract(text, Type.NEXT_WORD));
    	ret.put(DataKey.PAYMENT_DUE.fieldName(), new MarkerBasedExtractor("paid no later than").extract(text, Type.NEXT_WORD));
    	
    	ret.put(DataKey.DESCRIPTION_OF_OFFENCE.fieldName(), new MarkerBasedExtractor("DESCRIPTION OF OFFENCE:").extract(text, Type.FOLLOWED_BY_EMPTY_LINE));
    	ret.put(DataKey.POLICE_DETACHMENT.fieldName(), new MarkerBasedExtractor("Police Detachment:").extract(text, Type.LINE));
    	ret.put(DataKey.OFFICER_UNIT_NUMBER.fieldName(), new MarkerBasedExtractor("Officer Unit Number:").extract(text, Type.NEXT_WORD));
    	ret.put(DataKey.DOB.fieldName(), new MarkerBasedExtractor("DOB:").extract(text, Type.NEXT_WORD));
    	ret.put(DataKey.ADDRESS.fieldName(), new MarkerBasedExtractor("ADDRESS:").extract(text, Type.LINE));
    	
    	return new ExtractedData(2, ret);
    }
    
    private void saveData(ExtractedData data) throws IOException {
    	String typedOutputFile = data.getFileType()  == 1 ? getOutputFile1() : getOutputFile2();
//        Files.append(flatten((String[]) data.getData().toArray(new String[0]), separator), new File(typedOutputFile), Charset.defaultCharset());
	}

//	private String extractWithTika(File file) throws IOException, TikaException {
//        return tika.parseToString(file);
//    }
//
//    private String extractWithPdfBox(File file) throws IOException {
//        String pdfText;
//        try (PDDocument doc = PDDocument.load(file)) {
//            PDFTextStripper stripper = new PDFTextStripper();
//            pdfText = stripper.getText(doc);
//        }
//        return pdfText;
//    }

	private List<String> extractTokensWithAspose(File file) {
		com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(file.getPath());
		com.aspose.pdf.TextFragmentAbsorber tfa = new com.aspose.pdf.TextFragmentAbsorber();
		TextExtractionOptions teo = new TextExtractionOptions(TextExtractionOptions.TextFormattingMode.Raw);
		tfa.setExtractionOptions(teo);
		pdfDocument.getPages().accept(tfa);
		// create TextFragment Collection instance
		com.aspose.pdf.TextFragmentCollection tfc = tfa.getTextFragments();
		List<String> tokens = new ArrayList<String>();
		for (int i = 1; i <= tfc.size(); i++) {
			String text = tfc.get_Item(i).getText();
			String token;
			if (text.trim().isEmpty()) {
				token = " ";
			} else {
				token = text.trim();
			}
			if (i > 1 && isProbablySameWord(tfc.get_Item(i - 1).getRectangle(), tfc.get_Item(i).getRectangle())) {
				token = tokens.get(tokens.size() - 1) + token;
				tokens.remove(tokens.size() - 1);
			} else if (i > 1 && isProbablyNewLine(tfc.get_Item(i - 1).getRectangle(), tfc.get_Item(i).getRectangle())) {
				boolean newParagraph = isProbablyNewParagraph(tfc.get_Item(i - 1).getRectangle(), tfc.get_Item(i).getRectangle());
				tokens.add(NEW_LINE);
				if (newParagraph) {
					tokens.add(PARAGRAPH);
				}
			}
			if (!isIgnoreWord(token)) {
				tokens.add(token);
			}
		}
		for (int i = 0; i < tokens.size(); i++) {
			if (!tokens.get(i).isEmpty() && !(tokens.get(i).charAt(0) == '\n')) {
				tokens.set(i, tokens.get(i).trim());
			}
		}
		return tokens;
	}

	private boolean isIgnoreWord(String token) {
		return token != null && token.contains("SECTOR");
	}

	private boolean isProbablyNewParagraph(Rectangle left, Rectangle right) {
		if (right.getLLY() + 10 <= left.getLLY() - left.getHeight()) {
			return true;
		}
		return false;
	}
	
	private boolean isProbablyNewLine(Rectangle left, Rectangle right) {
		if (right.getLLY() <= left.getLLY() - left.getHeight()) {
			return true;
		}
		return false;
	}

	private boolean isProbablySameWord(Rectangle left, Rectangle right) {
		if (right.getLLX() - left.getURX() <= 0.2 && Math.abs(left.getLLY() - right.getLLY()) <= 1) {
			return true;
		}
		return false;
	}

	private String extractWithAspose(File file) {
        String extractedText = "Text from file " + file.getPath() + " could not be extracted";
        try {
            // Open document
            com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(file.getPath());

            // Create TextAbsorber object to extract text
            com.aspose.pdf.TextAbsorber textAbsorber = new com.aspose.pdf.TextAbsorber();
            
            // Accept the absorber for all the pages
            pdfDocument.getPages().accept(textAbsorber);

            // Get the extracted text
            extractedText = textAbsorber.getText();
//        System.out.println("extractedText=\n" + extractedText);

        } catch (Exception e) {
        	e.printStackTrace();
            //logger.warn("Problem extracting PDF from {}", file.getPath(, e));
            int x = 0;
        }
        return extractedText;
    }

    private void initAsposeLicense() {
        com.aspose.pdf.License license = new com.aspose.pdf.License();
        try {
            license.setLicense(ReportExtractor.class.getResourceAsStream("/Aspose.Pdf.lic"));
        } catch (Exception e) {
            logger.error("Aspose license problem", e);
        }
    }

    private String flatten(String[] values, String separator) {
        logger.debug("Flattening {} keys", values.length);
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            logger.debug(value.trim());
            builder.append(value.trim()).append(separator);
        }
        if (values.length > 0) {
            builder.delete(builder.length() - 1, builder.length());
        }
        return builder.toString() + "\n";
    }

    /**
     * @return the outputFile
     */
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * @return the outputFile of type 1
     */
    public String getOutputFile1() {
        int dot = outputFile.lastIndexOf(".");
        return new StringBuffer(outputFile).insert(dot, 1).toString();
    }

    /**
     * @return the outputFile of type 2
     */
    public String getOutputFile2() {
        int dot = outputFile.lastIndexOf(".");
        return new StringBuffer(outputFile).insert(dot, 2).toString();
    }

    /**
     * @return the outputKeyFile
     */
    public String getOutputKeyFile() {
        int dot = outputFile.lastIndexOf(".");
        return new StringBuffer(outputFile).insert(dot, "key").toString();
    }

    /**
     * @param outputFile the outputFile to set
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * @return the inputDir
     */
    public String getInputDir() {
        return inputDir;
    }

    /**
     * @param inputDir the inputDir to set
     */
    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    /**
     * @return the docCount
     */
    public int getDocCount() {
        return docCount;
    }

    /**
     * @param docCount the docCount to set
     */
    public void setDocCount(int docCount) {
        this.docCount = docCount;
    }

    private String sanitize(String marker, String str) {
        String value = str.replaceAll("\\s+", " ");
        if (CASE_NUMBER.equalsIgnoreCase(marker)) {
            currentCaseNumber = value;
        }
        if (SUMMARY.equalsIgnoreCase(marker)) {
            int nameStart = value.indexOf(" -");
            if (nameStart >= 0) {
                nameStart += 2;
                String name = getUpperCase(value, nameStart);
                currentKeyEntry = new KeyEntry(name, currentCaseNumber);
                value = value.replaceAll(name, currentKeyEntry.getHashKey());
            }
        }
        return value = "\""
                + value.trim()
                + "\"";
    }

    private String getUpperCase(String str, int start) {
        StringBuilder builder = new StringBuilder();
        int end = start;
        char c = str.charAt(end);
        while (end < str.length() && (Character.isUpperCase(c) || c == ' ' || c == '\n')) {
            builder.append(str.charAt(end));
            ++end;
            c = str.charAt(end);
        }
        return builder.toString().trim();
    }

    private int determineFileType(String pdfText) {
        return !pdfText.contains(markers2[0]) ? 1 : 2;
    }
}
