package com.hyperiongray.rcmp;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        "FIRST NAME:",};

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
                        extractInfo(file);
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
            Files.append(flatten((String[]) values, separator),
                    new File(getOutputKeyFile()), Charset.defaultCharset());
        }
    }

    private void extractInfo(File file) throws IOException, TikaException {
        //String pdfText = extractWithTika(file);
        //String pdfText = extractWithPdfBox(file);
        currentCaseNumber = "";
        currentKeyEntry = null;
        String pdfText = extractWithAspose(file);
        extractData(pdfText);
        logger.debug("File: {}", file.getPath());
        logger.trace(pdfText);
    }

    private void extractData(String pdfText) throws IOException {
        ArrayList<String> values = new ArrayList<>();
        int fileType = determineFileType(pdfText);
        String[] markers = fileType == 1 ? markers1 : markers2;
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
        String typedOutputFile = fileType == 1 ? getOutputFile1() : getOutputFile2();
        Files.append(flatten((String[]) values.toArray(new String[0]), separator),
                new File(typedOutputFile), Charset.defaultCharset());
        // form privacy entry
        if (currentKeyEntry != null) {
            KeyTable.getInstance().put(currentKeyEntry);
        }
    }

    private String extractWithTika(File file) throws IOException, TikaException {
        return tika.parseToString(file);
    }

    private String extractWithPdfBox(File file) throws IOException {
        String pdfText;
        try (PDDocument doc = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            pdfText = stripper.getText(doc);
        }
        return pdfText;
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
