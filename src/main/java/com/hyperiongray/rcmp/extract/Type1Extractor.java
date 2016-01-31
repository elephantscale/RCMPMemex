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
		ret.put(DataKey.OCCURRENCE_TYPE, extractor.extract("Occurrence Type:", "Occurrence Time:", pdfText));
		ret.put(DataKey.OCCURRENCE_TIME, extractor.extract("Occurrence Time:", "Repor:", pdfText));
		ret.put(DataKey.REPORTED_TIME, extractor.extract("Reported time:", "Occurrence Type:", pdfText));
		ret.put(DataKey.PLACE_OF_OFFENCE, extractor.extract("Place of offence:", "Occurrence Type:", pdfText));
		ret.put(DataKey.CLEARANCE_STATUS, extractor.extract("Clearance status:", "Occurrence Type:", pdfText));
		ret.put(DataKey.CONCLUDED, extractor.extract("Concluded:", "Occurrence Type:", pdfText));
		ret.put(DataKey.CONCLUDED_DATE, extractor.extract("Concluded date:", "Occurrence Type:", pdfText));
		ret.put(DataKey.SUMMARY, extractor.extract("Summary:", "Occurrence Type:", pdfText));
		ret.put(DataKey.REMARKS, extractor.extract("Remarks:", "Occurrence Type:", pdfText));
		ret.put(DataKey.ASSOCIATED_OCCURRENCES, extractor.extract("Associated occurrences:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_PERSONS, extractor.extract("Involved persons:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_ADDRESSES, extractor.extract("Involved addresses:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_COMM_ADDRESSES, extractor.extract("Involved comm addresses:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_VEHICLES, extractor.extract("Involved vehicles:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_OFFICERS, extractor.extract("Involved officers:", "Occurrence Type:", pdfText));
		ret.put(DataKey.INVOLVED_PROPERTY, extractor.extract("Involved property:", "Occurrence Type:", pdfText));
		ret.put(DataKey.MODUS_OPERANDI, extractor.extract("Modus operandi:", "Occurrence Type:", pdfText));
		ret.put(DataKey.REPORTS, extractor.extract("Reports:", "Occurrence Type:", pdfText));
		ret.put(DataKey.SUPPLEMENTARY_REPORT, extractor.extract("Supplementary report:", "Occurrence Type:", pdfText));
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
