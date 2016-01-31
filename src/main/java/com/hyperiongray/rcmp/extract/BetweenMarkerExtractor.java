package com.hyperiongray.rcmp.extract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetweenMarkerExtractor {
	private final static Logger logger = LoggerFactory.getLogger(Type1Extractor.class);

	public String extract(String marker, String nextMarker, String pdfText) {
		int markerStart = pdfText.indexOf(marker);
		String value = "";
		if (markerStart >= 0) {
			int markerStartNext = pdfText.indexOf(nextMarker);
			if (markerStartNext > 0) {
				String betweenTheMarkers = pdfText.substring(markerStart + marker.length(), markerStartNext).trim();
				logger.debug("Marker: {}", marker);
				logger.debug("Following text: {}", betweenTheMarkers);
				value = sanitize(marker, betweenTheMarkers);
			}
		}
		return value;
	}
	
	private String sanitize(String marker, String str) {
		String value = str.replaceAll("\\s+", " ");
		return value.trim();
	}
	
}
