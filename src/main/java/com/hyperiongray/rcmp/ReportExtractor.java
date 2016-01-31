package com.hyperiongray.rcmp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.hyperiongray.rcmp.extract.DataKey;
import com.hyperiongray.rcmp.extract.OutputColumns;
import com.hyperiongray.rcmp.extract.Type1Extractor;
import com.hyperiongray.rcmp.extract.Type2Extractor;

/**
 *
 * @author mark
 */
public class ReportExtractor {

    private final static Logger logger = LoggerFactory.getLogger(ReportExtractor.class);
	private static final boolean OBFUSCATE_NAMES = true;
    
    public static final String NEW_LINE = "#newline";
	public static final String PARAGRAPH = "#paragraph";
    
    private final String[] key_fileColumns = {
        "Person name",
        "Ticket number",
        "Officer name",
        "Offender name",
        "Unique key                        "  // spaces for Excel cell formatting
    };

    private String outputFile;
    private String inputDir;
    
    // not lazy initialization, to avoid threading problems
    private static final ReportExtractor instance = new ReportExtractor();
    
    private int docCount;
    private final static String separator = "|";

    public static ReportExtractor getInstance() {
        return instance;
    }

    private ReportExtractor() {
        // singleton
        initAsposeLicense();
    }

    public void doConvert() throws IOException {
        logger.info("Preparing to convert here: {}, output results there: {}", inputDir, outputFile);
        createOutputFiles();
        // for each report, add extracted information to the appropriate target files
        File[] files = new File(getInputDir()).listFiles();
        for (File file : files) {
            try {
                if (file.isFile() && file.exists()) {
                    String fileName = file.getName();
                    if (fileName.length() > 4 && ".pdf".equalsIgnoreCase(fileName.substring(fileName.length() - 4))) {
                        ExtractedData data = extractInfo(file);
                        String personName = getPersonNameFromSummary(data.getData().get(DataKey.SUMMARY));
                        if (StringUtils.isEmpty(personName)) { // type2 case
                        	if (!StringUtils.isEmpty(data.getData().get(DataKey.FIRST_NAME)) && !StringUtils.isEmpty(data.getData().get(DataKey.LAST_NAME))) {
                        		personName = data.getData().get(DataKey.FIRST_NAME) + " " + data.getData().get(DataKey.LAST_NAME);
                        	}
                        }
                        String ticketNumber = data.getData().get(DataKey.REPORT_NO);
                        String officerName = data.getData().get(DataKey.OFFICER_NOTES_ISSUING_OFFICER);
                        String offenderName = data.getData().get(DataKey.OFFICER_NOTES_OFFENDER);
                        KeyEntry keyEntry = null;
                        if (!StringUtils.isEmpty(ticketNumber)) {
	                        keyEntry = new KeyEntry(personName, ticketNumber, officerName, offenderName);
	                        KeyTable.getInstance().put(keyEntry);
                        } else {
                        	logger.warn("No person name or ticket number found for file " + fileName);
                        }
                        if (keyEntry != null && OBFUSCATE_NAMES) {
                        	obfuscate(data, DataKey.FIRST_NAME, keyEntry.getHashKey());
                        	obfuscate(data, DataKey.LAST_NAME, keyEntry.getHashKey());
                        	obfuscate(data, DataKey.OFFICER_NOTES_ISSUING_OFFICER, keyEntry.getHashKey());
                        	obfuscate(data, DataKey.OFFICER_NOTES_OFFENDER, keyEntry.getHashKey());
                        	if (!StringUtils.isEmpty(data.getData().get(DataKey.SUMMARY))) {
                        		String value = data.getData().get(DataKey.SUMMARY);
                        		data.getData().put(DataKey.SUMMARY, value.replaceAll(keyEntry.getPersonName(), keyEntry.getHashKey()));
                        	}
                        }
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

    private void obfuscate(ExtractedData data, DataKey key, String hash) {
    	if (!StringUtils.isEmpty(data.getData().get(key))) {
    		data.getData().put(key, hash);
    	}
	}

	private void createOutputFiles() throws IOException {
        new File(getOutputFile1()).delete();
        Files.append(flatten(converToColumnNames(OutputColumns.TYPE_1), separator), new File(getOutputFile1()), Charset.defaultCharset());
        new File(getOutputFile2()).delete();
        Files.append(flatten(converToColumnNames(OutputColumns.TYPE_2), separator), new File(getOutputFile2()), Charset.defaultCharset());
        logger.info("Will output into two files: {} and {}", getOutputFile1(), getOutputFile2());
    }

    private void writeKeyFile() throws IOException {
        logger.info("Writing the key file: {}", getOutputKeyFile());
        new File(getOutputKeyFile()).delete();
        Files.append(flatten(key_fileColumns, separator), new File(getOutputKeyFile()), Charset.defaultCharset());
        Map<String, KeyEntry> keyTable = KeyTable.getInstance().getKeyTable();
        Iterator<String> iter = keyTable.keySet().iterator();
        String[] values = new String[5];
        while (iter.hasNext()) {
            KeyEntry entry = keyTable.get(iter.next());
            values[0] = entry.getPersonName();
            values[1] = entry.getTicketNumber();
            values[2] = entry.getOfficerName();
            values[3] = entry.getOffenderName();
            values[4] = entry.getHashKey();
            Files.append(flatten((String[]) values, separator), new File(getOutputKeyFile()), Charset.defaultCharset());
        }
    }

    private String[] converToColumnNames(DataKey[] dataKeys) {
    	List<String> ret = new ArrayList<String>();
    	for (int i = 0; i < dataKeys.length; i++) {
    		ret.add(dataKeys[i].fieldName());
    	}
		return ret.toArray(new String[ret.size()]);
	}

	public ExtractedData extractInfo(File file) throws IOException, TikaException {
        String pdfText = extractWithAspose(file);
        int fileType = determineFileType(pdfText);
        ExtractedData extractedData;
        if (fileType == 1) {
        	extractedData = new Type1Extractor().extractData(pdfText);
        } else if (fileType == 2) {
        	extractedData = new Type2Extractor().extractData(file);
        } else {
        	throw new IllegalStateException("Unknown file type " + fileType);
        }
        logger.debug("File: {}", file.getPath());
        logger.trace(pdfText);
        return extractedData;
    }

    private void saveData(ExtractedData data) throws IOException {
    	String typedOutputFile = data.getFileType()  == 1 ? getOutputFile1() : getOutputFile2();
    	DataKey[] columns = data.getFileType() == 1 ? OutputColumns.TYPE_1 : OutputColumns.TYPE_2;
    	List<String> values = new ArrayList<String>();
    	for (int i = 0; i < columns.length; i++) {
    		String value = null;
    		if (data.getData().containsKey(columns[i])) {
    			value = data.getData().get(columns[i]);
    		}
    		values.add(value != null ? value : "");
    	}
        Files.append(flatten((String[]) values.toArray(new String[0]), separator), new File(typedOutputFile), Charset.defaultCharset());
	}

	private String extractWithAspose(File file) {
        String extractedText = "Text from file " + file.getPath() + " could not be extracted";
        try {
            com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(file.getPath());
            com.aspose.pdf.TextAbsorber textAbsorber = new com.aspose.pdf.TextAbsorber();
            pdfDocument.getPages().accept(textAbsorber);
            extractedText = textAbsorber.getText();
        } catch (Exception e) {
        	logger.error("Problem extracting PDF from " + file.getPath(), e);
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
            logger.debug(Utils.notNull(value).trim());
            builder.append(Utils.notNull(value).trim()).append(separator);
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

    private String getPersonNameFromSummary(String value) {
    	 if (value == null) {
    		return "";
    	 }
    	 int nameStart = value.indexOf(" -");
         if (nameStart >= 0) {
	           nameStart += 2;
	           String name = Utils.getUpperCase(value, nameStart);
	           return name;
         }
         return "";
    }
    
//    private String sanitize(String marker, String str) {
//        String value = str.replaceAll("\\s+", " ");
//        if (CASE_NUMBER.equalsIgnoreCase(marker)) {
//            currentCaseNumber = value;
//        }
//        if (SUMMARY.equalsIgnoreCase(marker)) {
//            int nameStart = value.indexOf(" -");
//            if (nameStart >= 0) {
//                nameStart += 2;
//                String name = getUpperCase(value, nameStart);
//                currentKeyEntry = new KeyEntry(name, currentCaseNumber);
//                value = value.replaceAll(name, currentKeyEntry.getHashKey());
//            }
//        }
//        return value = "\""
//                + value.trim()
//                + "\"";
//    }

    private int determineFileType(String pdfText) {
        return !pdfText.contains( "TICKET   NO:") ? 1 : 2;
    }
}


