package com.hyperiongray.rcmp;

import java.util.Map;

import com.hyperiongray.rcmp.extract.DataKey;

public class ExtractedData {

	private int fileType;
	private Map<DataKey, String> data;

	// get
	public int getFileType() { return fileType; }
	public Map<DataKey, String> getData() { return data; }
	
	public ExtractedData(int fileType, Map<DataKey, String> data) {
		this.fileType = fileType;
		this.data = data;
	}

}
