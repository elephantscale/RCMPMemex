package com.hyperiongray.rcmp;

import java.util.List;

import com.aspose.pdf.Rectangle;

public class Utils {

	public static String join(List<String> tokens) {
		StringBuilder ret = new StringBuilder();
		for (String token : tokens) {
			ret.append(token);
			ret.append(" ");
		}
		return ret.toString();
	}

	public static String trim(String text) {
		if (text == null) {
			return null;
		}
		return text.trim();
	}
	
	public static String getUpperCase(String str, int start) {
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

	public static String notNull(String value) {
		return value == null ? "" : value;
	}
	
	public static boolean isIgnoreWord(String token) {
		return token != null && token.contains("SECTOR");
	}

	public static boolean isProbablyNewParagraph(Rectangle left, Rectangle right) {
		if (right.getLLY() + 10 <= left.getLLY() - left.getHeight()) {
			return true;
		}
		return false;
	}
	
	public static boolean isProbablyNewLine(Rectangle left, Rectangle right) {
		if (right.getLLY() <= left.getLLY() - left.getHeight()) {
			return true;
		}
		if (right.getLLY() >= left.getLLY() + 10) {
			return true;
		}
		if (left.getLLX() + left.getWidth() + 10 < right.getLLX()) {
			return true;
		}
		if (right.getLLX() + right.getWidth() + 10 < left.getLLX()) {
			return true;
		}
		return false;
	}

	public static boolean isProbablySameWord(Rectangle left, Rectangle right) {
		if (right.getLLX() - left.getURX() <= 0.2 && Math.abs(left.getLLY() - right.getLLY()) <= 1) {
			return true;
		}
		return false;
	}
	 
	
}
