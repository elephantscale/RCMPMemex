package com.hyperiongray.rcmp;

import java.util.Map;

public class ExtractedData {

	private int fileType;
	private Map<String, String> data;

	// get
	public int getFileType() { return fileType; }
	public Map<String, String> getData() { return data; }
	
	public ExtractedData(int fileType, Map<String, String> data) {
		this.fileType = fileType;
		this.data = data;
	}

}
