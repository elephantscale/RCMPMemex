package com.hyperiongray.rcmp.extract;

public abstract class Criteria {

	
	public abstract boolean accept(String text, String term);

	public static Criteria after(final String firstTerm) {
		return new Criteria() {
			@Override public boolean accept(String text, String term) {
				int firstIndex = text.indexOf(firstTerm);
				int termIndex = text.indexOf(term);
				return firstIndex <= termIndex;
			}
		};
	}
	
	public static Criteria all() {
		return new Criteria() {
			@Override public boolean accept(String text, String term) {
				return true;
			}
		};
	}
	
}
