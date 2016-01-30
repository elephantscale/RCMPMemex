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
}
