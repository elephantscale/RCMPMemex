package com.hyperiongray.rcmp;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.tika.exception.TikaException;
import org.junit.Before;
import org.junit.Test;

import com.hyperiongray.rcmp.extract.DataKey;

public class ReportExtractorTest {

	private ReportExtractor reportExtractor;
	
	@Before
	public void setup() {
		reportExtractor = ReportExtractor.getInstance();
	}
	
	@Test
	public void testType1() throws IOException, TikaException {
		System.out.println("Started type1 test");
		File file = new File("sample_data/type1/PROS 2.pdf");
		ExtractedData data = reportExtractor.extractInfo(file);
		Map<DataKey, String> extractedData = data.getData();
		System.out.println("Extracted " + extractedData.size() + " fields.");
		for (DataKey key : extractedData.keySet()) {
			System.out.println(key + " >> " + extractedData.get(key));
		}
	}
	
	@Test
	public void testType2() throws IOException, TikaException {
		System.out.println("Started type2 test");
		File file = new File("sample_data/type2/6 ticket.pdf");
		ExtractedData data = reportExtractor.extractInfo(file);
		Map<DataKey, String> extractedData = data.getData();
		System.out.println("Extracted " + extractedData.size() + " fields.");
		for (DataKey key : extractedData.keySet()) {
			System.out.println(key + " >> " + extractedData.get(key));
		}
	}
	
}