package com.hyperiongray.rcmp.extract;

import com.hyperiongray.rcmp.ReportExtractor;

public class MarkerBasedExtractor {

	private String marker;
	
	public MarkerBasedExtractor(String marker) {
		this.marker = marker;
	}

	public String extract(String text, Type extractType) {
		int pos = text.indexOf(marker);
		if (pos < 0) {
			return null;
		}
		int lastIndex = 0;
		if (extractType == Type.LINE) {
			int newLine = text.indexOf(ReportExtractor.NEW_LINE, pos + 1);
			if (newLine == -1) {
				newLine = text.length();
			}
			lastIndex = newLine;
		} else if (extractType == Type.NEXT_WORD) {
			lastIndex = pos + marker.length();
			lastIndex = skipWhitespace(lastIndex, text);
			lastIndex = skipWord(lastIndex, text);
		} else if (extractType == Type.FOLLOWED_BY_EMPTY_LINE) {
			lastIndex = pos + marker.length();
			int nextParagraph = text.indexOf(ReportExtractor.PARAGRAPH, lastIndex);
			if (nextParagraph < 0) {
				nextParagraph = text.length();
			}
			lastIndex = nextParagraph;
		} else {
			throw new IllegalStateException("Type " + extractType + " is not implemented.");
		}
		String value = text.substring(pos + marker.length(), lastIndex);
		return sanitize(value);
	}
	
	private int skipWord(int index, String text) {
		while (index < text.length() && !Character.isWhitespace(text.charAt(index))) {
			index = skipMetaword(index, text);
			index++;
		}
		return index;
	}

	private int skipWhitespace(int index, String text) {
		while (index < text.length() && Character.isWhitespace(text.charAt(index))) {
			index = skipMetaword(index, text);
			index++;
		}
		return index;
	}

	private int skipMetaword(int index, String text) {
		if (text.charAt(index) == '#') {
			if (text.indexOf(ReportExtractor.NEW_LINE, index) == index) {
				index += ReportExtractor.NEW_LINE.length();
			}
			if (text.indexOf(ReportExtractor.PARAGRAPH, index) == index) {
				index += ReportExtractor.PARAGRAPH.length();
			}
		}
		return index;
	}

	private String sanitize(String value) {
		if (value == null) {
			return null;
		}
		value = value.replaceAll(ReportExtractor.NEW_LINE, " ");
		value = value.replaceAll(ReportExtractor.PARAGRAPH, " ");
		value = value.replaceAll("\\s+", " ");
		value = value.trim();
		return value;
	}

	public enum Type {
		LINE,
		NEXT_WORD,
		FOLLOWED_BY_EMPTY_LINE
	}

}
