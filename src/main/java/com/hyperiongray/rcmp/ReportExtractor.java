package com.hyperiongray.rcmp;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
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

    private final String[] markers2 = {
        "TICKET NO:",};

    private String outputFile;
    private String inputDir;
    // not lazy initialization, to avoid threading problems
    private static final ReportExtractor instance = new ReportExtractor();
    private int docCount;
    private final static String separator = "|";

    private boolean debug = false;

    private int fileType = 0;

    public static ReportExtractor getInstance() {
        return instance;
    }

    private ReportExtractor() {
        // singleton
        initAsposeLicense();
    }

    public void doConvert() throws IOException {
        new File(getOutputFile1()).delete();
        Files.append(flatten(markers1, separator), new File(getOutputFile1()), Charset.defaultCharset());
        new File(getOutputFile2()).delete();
        Files.append(flatten(markers2, separator), new File(getOutputFile2()), Charset.defaultCharset());
        File[] files = new File(getInputDir()).listFiles();
        for (File file : files) {
            try {
                if (file.isFile() && file.exists()) {
                    extractInfo(file);
                    ++docCount;
                }
            } catch (IOException | TikaException e) {
                logger.error("Problem converting file {}", file.getName(), e);
            }
        }
    }

    private void extractInfo(File file) throws IOException, TikaException {
        //String pdfText = extractWithTika(file);
        //String pdfText = extractWithPdfBox(file);
        String pdfText = extractWithAspose(file);
        extractData(pdfText);
        if (isDebug()) {
            System.out.println("File: " + file.getPath() + " ++++++++++++++++++++++++++++");
            System.out.println(pdfText);
        }
    }

    private void extractData(String pdfText) throws IOException {
        ArrayList<String> values = new ArrayList<>();
        for (int m = 0; m < markers1.length; ++m) {
            String marker = markers1[m];
            String value = "";
            int markerStart = pdfText.indexOf(marker);
            if (markerStart >= 0) {
                if (m < markers1.length - 1) {
                    String nextMarker = markers1[m + 1];
                    int markerStartNext = pdfText.indexOf(nextMarker);
                    if (markerStartNext > 0) {
                        String betweenTheMarkers = pdfText.substring(markerStart + marker.length(), markerStartNext).trim();
                        if (isDebug()) {
                            System.out.println("Marker: \n" + marker);
                            System.out.println("Following text: \n" + betweenTheMarkers);
                        }
                        value = sanitize(betweenTheMarkers);
                    }

                }
            }
            values.add(value);
        }
        Files.append(flatten((String[]) values.toArray(new String[0]), separator),
                new File(getOutputFile()), Charset.defaultCharset());
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

    private String extractWithAspose(File file) throws IOException {
        // Open document
        com.aspose.pdf.Document pdfDocument = new com.aspose.pdf.Document(file.getPath());

        // Create TextAbsorber object to extract text
        com.aspose.pdf.TextAbsorber textAbsorber = new com.aspose.pdf.TextAbsorber();

        // Accept the absorber for all the pages
        pdfDocument.getPages().accept(textAbsorber);

        // Get the extracted text
        String extractedText = textAbsorber.getText();
//        System.out.println("extractedText=\n" + extractedText);
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
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
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

    private String sanitize(String str) {
        return "\""
                + str.trim()
                + "\"";
    }

    /**
     * @return the debug
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @param debug the debug to set
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
