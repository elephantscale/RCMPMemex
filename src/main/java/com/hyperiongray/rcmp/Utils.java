package com.hyperiongray.rcmp;

import java.util.List;

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
	
}
