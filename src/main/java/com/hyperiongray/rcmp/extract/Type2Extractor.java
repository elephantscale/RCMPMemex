package com.hyperiongray.rcmp.extract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hyperiongray.rcmp.ExtractedData;
import com.hyperiongray.rcmp.Utils;
import com.hyperiongray.rcmp.extract.MarkerBasedExtractor.Type;

public class Type2Extractor {

	 public ExtractedData extractData(List<String> tokens) {
	    	Map<DataKey, String> ret = new HashMap<DataKey, String>();
	    	String text = Utils.join(tokens);
	    	ret.put(DataKey.REPORT_NO, new MarkerBasedExtractor("TICKET NO:").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.LAST_NAME, new MarkerBasedExtractor("LAST NAME:").extract(text, Type.LINE));
	    	ret.put(DataKey.FIRST_NAME, new MarkerBasedExtractor("FIRST NAME:").extract(text, Type.LINE));
	    	ret.put(DataKey.SEX, new MarkerBasedExtractor("SEX:").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.DRIVERS_LICENCE_NO, new MarkerBasedExtractor("LICENCE NO.:").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.DRIVERS_LICENCE_PROV, new MarkerBasedExtractor("LICENCE PROV:").extract(text, Type.LINE));
	    	ret.put(DataKey.VEHICLE_MAKE, new MarkerBasedExtractor("Make:").extract(text, Type.LINE));
	    	ret.put(DataKey.VEHICLE_MODEL, new MarkerBasedExtractor("Model:").extract(text, Type.LINE));
	    	ret.put(DataKey.VEHICLE_YEAR, new MarkerBasedExtractor("Year:").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.VEHICLE_LICENCE_NO, new MarkerBasedExtractor("Licence No.:").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.VEHICLE_PROVINCE, new MarkerBasedExtractor("Province:").extract(text, Type.LINE));
	    	ret.put(DataKey.VEHICLE_OWNERS_NAME, new MarkerBasedExtractor("Owner's Name:").extract(text, Type.LINE));
	    	ret.put(DataKey.VEHICLE_OWNER_ADDRESS, new MarkerBasedExtractor("Address:").extract(text, Type.LINE));
	    	ret.put(DataKey.VEHICLE_EXP_YEAR, new MarkerBasedExtractor("Exp. Year:").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.CLOCKED_SPEED, new RegexpExtractor("Vehicle was clocked at (.*?) km").extract(text));
	    	ret.put(DataKey.SPEED_LIMIT_EXCEEDED, new RegexpExtractor("Exceed Speed Limit of (.*?) km").extract(text));   
	    	ret.put(DataKey.DATE, new RegexpExtractor("On ((.){0,20} at [0-9:]{0,10}+ (AM|PM)?)").extract(text));
	    	ret.put(DataKey.PAYMENT_OPTION, new MarkerBasedExtractor("A payment option of").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.PAYMENT_DUE, new MarkerBasedExtractor("paid no later than").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.DESCRIPTION_OF_OFFENCE, new MarkerBasedExtractor("DESCRIPTION OF OFFENCE:").extract(text, Type.FOLLOWED_BY_EMPTY_LINE));
	    	ret.put(DataKey.POLICE_DETACHMENT, new MarkerBasedExtractor("Police Detachment:").extract(text, Type.LINE));
	    	ret.put(DataKey.OFFICER_UNIT_NUMBER, new MarkerBasedExtractor("Officer Unit Number:").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.DOB, new MarkerBasedExtractor("DOB:").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.ADDRESS, new MarkerBasedExtractor("ADDRESS:").extract(text, Type.LINE));
	    	// Officer notes section
	    	ret.put(DataKey.OFFICER_NOTES_ISSUING_OFFICER, new MarkerBasedExtractor("Issuing/Informant Officer:").extract(text, Type.LINE));
	    	ret.put(DataKey.OFFICER_ID, new MarkerBasedExtractor("ID:").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.OFFICER_DETACHMENT, new MarkerBasedExtractor("Detachment or Agency:").extract(text, Type.LINE));
	    	ret.put(DataKey.OFFICER_NOTES_CREATION_DATE, new MarkerBasedExtractor("Notes Creation Date and Time:").extract(text, Type.LINE));
	    	ret.put(DataKey.OFFICER_NOTES_OFFENDER, new MarkerBasedExtractor("Offender:").extract(text, Type.LINE));
	    	ret.put(DataKey.TICKET_DELIVERED_AND_SERVED, new MarkerBasedExtractor("Ticket Delivered and Served:").extract(text, Type.NEXT_WORD));
	    	ret.put(DataKey.IDENTIFIED_BY, new MarkerBasedExtractor("Accused identified by:").extract(text, Type.LINE));
	    	ret.put(DataKey.HEIGHT, new MarkerBasedExtractor("Height:").extract(text, Type.LINE));
	    	ret.put(DataKey.WEIGHT, new MarkerBasedExtractor("Weight:").extract(text, Type.LINE));
	    	ret.put(DataKey.EYE_COLOUR, new MarkerBasedExtractor("Eye Colour:").extract(text, Type.LINE));
	    	ret.put(DataKey.HAIR_COLOUR, new MarkerBasedExtractor("Hair Colour:").extract(text, Type.LINE));
	    	ret.put(DataKey.OFFENCE_IDENTIFICATION, new MarkerBasedExtractor("Offence Identification:").extract(text, Type.LINE));
	    	ret.put(DataKey.MARKS_OTHER_DESCRIPTIONS, new MarkerBasedExtractor("Marks/Other Descriptors:").extract(text, Type.LINE));
	    	ret.put(DataKey.ATTITUDE, new MarkerBasedExtractor("Attitude:").extract(text, Type.LINE));
	    	ret.put(DataKey.P_C_NO, new MarkerBasedExtractor("P.C. No:").extract(text, Type.LINE));
	    	ret.put(DataKey.P_C_MODE, new MarkerBasedExtractor("P.C. Mode:").extract(text, Type.LINE));
	    	ret.put(DataKey.P_C_DIRECTION, new MarkerBasedExtractor("P.C. Direction:").extract(text, Type.LINE));
	    	ret.put(DataKey.SETUP_NEAR, new MarkerBasedExtractor("Setup Near:").extract(text, Type.LINE));
	    	ret.put(DataKey.LATITUDE, new MarkerBasedExtractor("Latitude:").extract(text, Type.LINE));
	    	ret.put(DataKey.LONGITUDE, new MarkerBasedExtractor("Longitude:").extract(text, Type.LINE));
	    	ret.put(DataKey.VIOLATOR_SPEED_READING, new MarkerBasedExtractor("Violator Speed Reading:").extract(text, Type.LINE));
	    	ret.put(DataKey.VIOLATOR_DIRECTION, new MarkerBasedExtractor("Violator Direction:").extract(text, Type.LINE));
	    	ret.put(DataKey.TRAFFIC_VIOLATOR_SPEED, new MarkerBasedExtractor("Traffic Violator Speed:").extract(text, Type.LINE));
	    	ret.put(DataKey.ADMITTED_SPEED, new MarkerBasedExtractor("Admitted Speed:").extract(text, Type.LINE));
	    	ret.put(DataKey.P_C_SPEED, new MarkerBasedExtractor("P.C. Speed:").extract(text, Type.LINE));
	    	ret.put(DataKey.WEATHER, new MarkerBasedExtractor("Weather:").extract(text, Type.LINE));
	    	ret.put(DataKey.VISIBILITY, new MarkerBasedExtractor("Visibility:").extract(text, Type.LINE));
	    	ret.put(DataKey.NATURAL_LIGHT, new MarkerBasedExtractor("Natural Light:").extract(text, Type.LINE));
	    	ret.put(DataKey.TRAFFIC_CONDITION, new MarkerBasedExtractor("Traffic Condition:").extract(text, Type.LINE));
	    	ret.put(DataKey.PEDESTRIAN_TRAFFIC, new MarkerBasedExtractor("Pedestrian Traffic:").extract(text, Type.LINE));
	    	ret.put(DataKey.NO_OF_PASSENGERS, new MarkerBasedExtractor("No. of Passengers:").extract(text, Type.LINE));
	    	ret.put(DataKey.ROAD_TYPE, new MarkerBasedExtractor("Road Type:").extract(text, Type.LINE));
	    	ret.put(DataKey.ROAD_SURFACE, new MarkerBasedExtractor("Road Surface:").extract(text, Type.LINE));
	    	ret.put(DataKey.ROAD_CONDITION, new MarkerBasedExtractor("Road Condition:").extract(text, Type.LINE));
	    	ret.put(DataKey.SPEED_CALCULATED_BY, new MarkerBasedExtractor("Speed Calculated by:").extract(text, Type.LINE));
	    	ret.put(DataKey.RADAR_SERIAL_NO, new MarkerBasedExtractor("Serial No.:", Criteria.after("Officer Notes")).extract(text, Type.LINE));
	    	ret.put(DataKey.RADAR_HEAD_DIRECTION, new MarkerBasedExtractor("Radar Head Direction:").extract(text, Type.LINE));
	    	ret.put(DataKey.RADAR_MAKE, new MarkerBasedExtractor("Make:", Criteria.after("Officer Notes")).extract(text, Type.LINE));
	    	ret.put(DataKey.RADAR_MODEL, new MarkerBasedExtractor("Model:", Criteria.after("Officer Notes")).extract(text, Type.LINE));
	    	
	    	return new ExtractedData(2, ret);
    }

}
