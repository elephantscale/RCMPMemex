package com.hyperiongray.rcmp.extract;

import com.hyperiongray.rcmp.ReportExtractor;


public class MarkerBasedExtractor {

	private String marker;
	private Criteria criteria;
	
	public MarkerBasedExtractor(String marker) {
		this(marker, Criteria.all());
	}
	
	public MarkerBasedExtractor(String marker, Criteria criteria) {
		this.marker = marker;
		this.criteria = criteria;
	}

	public String extract(String text, Type extractType) {
		int index = 0;
		while (true) {
			int pos = text.indexOf(marker, index);
			index = find(text, extractType, index);
			if (index < 0) {
				return null;
			}
			String value = text.substring(pos + marker.length(), index);
			if (criteria.accept(text, value)) {
				return sanitize(value);
			}
		}
	}
	
	private int find(String text, Type extractType, int findFrom) {
		int pos = text.indexOf(marker, findFrom);
		if (pos < 0) {
			return -1;
		}
		int lastIndex = 0;
		if (extractType == Type.LINE) {
			int newLine = text.indexOf(ReportExtractor.SEPARATOR, pos + 1);
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
		} else if (extractType == Type.UNTIL_NEXT_PARAGRAPH) {
			lastIndex = pos + marker.length();
			lastIndex = skipWhitespace(lastIndex, text);
			lastIndex = skipMetaword(lastIndex, text);
			lastIndex = skipWhitespace(lastIndex, text);
			int newLine = text.indexOf(ReportExtractor.PARAGRAPH, lastIndex + 1);
			if (newLine == -1) {
				newLine = text.length();
			}
			lastIndex = newLine;
		} else {
			throw new IllegalStateException("Type " + extractType + " is not implemented.");
		}
		return lastIndex;
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
			if (text.indexOf(ReportExtractor.SEPARATOR, index) == index) {
				index += ReportExtractor.SEPARATOR.length();
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
		value = value.replaceAll(ReportExtractor.SEPARATOR, " ");
		value = value.replaceAll(ReportExtractor.PARAGRAPH, " ");
		value = value.replaceAll("\\s+", " ");
		value = value.trim();
		return value;
	}

	public enum Type {
		LINE,
		NEXT_WORD,
		FOLLOWED_BY_EMPTY_LINE,
		UNTIL_NEXT_PARAGRAPH
	}

}
