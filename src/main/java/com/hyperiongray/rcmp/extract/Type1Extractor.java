package com.hyperiongray.rcmp.extract;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hyperiongray.rcmp.ExtractedData;

public class Type1Extractor {

    private final static Logger logger = LoggerFactory.getLogger(Type1Extractor.class);
    
	public ExtractedData extractData(String pdfText) throws IOException {
		Map<DataKey, String> ret = new HashMap<DataKey, String>();
		BetweenMarkerExtractor extractor = new BetweenMarkerExtractor();
		ret.put(DataKey.REPORT_NO, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.OCCURRENCE_TYPE, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.OCCURRENCE_TIME, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.REPORETD_TIME, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.PLACE_OF_OFFENCE, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.CLEARANCE_STATUS, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.CONCLUDED, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.CONCLUDED_DATE, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.SUMMARY, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.REMARKS, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.ASSOCIATED_OCCURRENCES, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_PERSONS, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_ADDRESSES, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_COMM_ADDRESSES, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_VEHICLES, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_OFFICERS, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_PROPERTY, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.MODUS_OPERANDI, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.REPORTS, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		ret.put(DataKey.SUPPLEMENTARY_REPORT, extractor.extract("Report No.:", "Occurrence Type:", pdfText));
		return new ExtractedData(1, ret);
	}
	
//	REPORT_NO("Report no.:"),
//    OCCURRENCE_TYPE("Occurrence Type:"),
//    OCCURRENCE_TIME("Occurrence time:"),
//    REPORETD_TIME("Reported time:"),
//    PLACE_OF_OFFENCE("Place of offence:"),
//    CLEARANCE_STATUS("Clearance status:"),
//    CONCLUDED("Concluded:"),
//    CONCLUDED_DATE("Concluded date:"),
//    SUMMARY("Summary:"),
//    REMARKS("Remarks:"),
//    ASSOCIATED_OCCURRENCES("Associated occurrences:"),
//    INVOLVED_PERSONS("Involved persons:"),
//    INVOLVED_ADDRESSES("Involved addresses:"),
//    INVOLVED_COMM_ADDRESSES("Involved comm addresses:"),
//    INVOLVED_VEHICLES("Involved vehicles:"),
//    INVOLVED_OFFICERS("Involved officers:"),
//    INVOLVED_PROPERTY("Involved property:"),
//    MODUS_OPERANDI("Modus operandi:"),
//    REPORTS("Reports:"),
//    SUPPLEMENTARY_REPORT("Supplementary report:"),
//    

}
