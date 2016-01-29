package com.hyperiongray.rcmp;

import java.util.List;

public class ExtractedData {

	private int fileType;
	private List<String> data;
	private String[] markers;

	// get
	public int getFileType() { return fileType; }
	public List<String> getData() { return data; }
	public String[] getMarkers() { return markers; }
	
	public ExtractedData(int fileType, String[] markers, List<String> data) {
		this.fileType = fileType;
		this.data = data;
		this.markers = markers;
	}

}
