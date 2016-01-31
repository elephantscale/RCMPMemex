package com.hyperiongray.rcmp.extract;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpExtractor {

	private String regexp;
	
	public RegexpExtractor(String regexp) {
		this.regexp = regexp;
	}

	public String extract(String text) {
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}
	
}
