package com.hyperiongray.rcmp.extract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetweenMarkerExtractor {
	private final static Logger logger = LoggerFactory.getLogger(Type1Extractor.class);

	public String extract(String marker, String nextMarkerRegexp, String pdfText) {
		int markerStart = pdfText.indexOf(marker);
		String value = "";
		if (markerStart >= 0) {
			int markerStartNext = indexOf(pdfText, nextMarkerRegexp, markerStart);
			if (markerStartNext > 0) {
				String betweenTheMarkers = pdfText.substring(markerStart + marker.length(), markerStartNext).trim();
				logger.debug("Marker: {}", marker);
				logger.debug("Following text: {}", betweenTheMarkers);
				value = sanitize(marker, betweenTheMarkers);
			}
		}
		return value;
	}
	
	private int indexOf(String text, String regexp, int fromIndex) {
		Pattern pattern = Pattern.compile(regexp);
	    Matcher matcher = pattern.matcher(text);
	    while (matcher.find()) {
	    	int start = matcher.start();
	    	if (start > fromIndex) {
	    		return start;
	    	}
	    }
	    return -1;
	}

	private String sanitize(String marker, String str) {
		String value = str.replaceAll("\\s+", " ");
		return value.trim();
	}
	
}
