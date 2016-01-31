package com.hyperiongray.rcmp.extract;

public enum DataKey {

	// type 1
	REPORT_NO("Report no"),
    OCCURRENCE_TYPE("Occurrence Type"),
    OCCURRENCE_TIME("Occurrence time"),
    REPORTED_TIME("Reported time"),
    PLACE_OF_OFFENCE("Place of offence"),
    CLEARANCE_STATUS("Clearance status"),
    CONCLUDED("Concluded"),
    CONCLUDED_DATE("Concluded date"),
    SUMMARY("Summary"),
    REMARKS("Remarks"),
    ASSOCIATED_OCCURRENCES("Associated occurrences"),
    INVOLVED_PERSONS("Involved persons"),
    INVOLVED_ADDRESSES("Involved addresses"),
    INVOLVED_COMM_ADDRESSES("Involved comm addresses"),
    INVOLVED_VEHICLES("Involved vehicles"),
    INVOLVED_OFFICERS("Involved officers"),
    INVOLVED_PROPERTY("Involved property"),
    MODUS_OPERANDI("Modus operandi"),
    REPORTS("Reports"),
    SUPPLEMENTARY_REPORT("Supplementary report"),
    
    // type 2
	LAST_NAME("Last Name"),
	FIRST_NAME("First Name"),
	SEX("Sex"),
	DRIVERS_LICENCE_NO("Driver's Licence No"),
	DRIVERS_LICENCE_PROV("Driver's Licence Prov"),
	DOB("DOB"),
	ADDRESS("Address"),
	VEHICLE_MAKE("Vehicle Make"),
	VEHICLE_MODEL("Vehicle Model"),
	VEHICLE_YEAR("Vehicle Year"),
	VEHICLE_LICENCE_NO("Vehicle Licence No"),
	VEHICLE_PROVINCE("Vehicle Province"),
	VEHICLE_OWNERS_NAME("Vehicle Owner"),
	VEHICLE_OWNER_ADDRESS("Vehicle Owner Address:"),
	DESCRIPTION_OF_OFFENCE("Description"),
	VEHICLE_EXP_YEAR("Vehicle Licence Exp. Year"),
	SPEED_LIMIT_EXCEEDED("Speed limit exceeded"),
	POLICE_DETACHMENT("Police Detachment"),
	OFFICER_UNIT_NUMBER("Officer Unit Number"),
	DATE("Date"),
	CLOCKED_SPEED("Vehicle clocked speed"),
	PAYMENT_OPTION("Payment option"),
	PAYMENT_DUE("Payment due"),
	LOCATION("Location"),
	OFFICER_NOTES_ISSUING_OFFICER("Officer"), 
	OFFICER_ID("Officer ID"),
	OFFICER_DETACHMENT("Officer Detachment/Agency"),
	OFFICER_NOTES_CREATION_DATE("Note Creation Date"),
	OFFICER_NOTES_OFFENDER("Offender:"),
	TICKET_DELIVERED_AND_SERVED("Ticket Delivered and Served:"),
	IDENTIFIED_BY("Identified By"),
	WEIGHT("Weight"),
	HEIGHT("Height"),
	EYE_COLOUR("Eye Colour"),
	HAIR_COLOUR("Hair Colour"),
	OFFENCE_IDENTIFICATION("Offence Identification"),
	MARKS_OTHER_DESCRIPTIONS("Marks/Other Descriptors"),
	ATTITUDE("Attitude"),
	P_C_NO("P.C.No"),
	P_C_MODE("P.C.Mode"),
	P_C_DIRECTION("P.C.Direction"),
	SETUP_NEAR("Setup Near"),
	LATITUDE("Latitude"),
	LONGITUDE("Longitude"),
	VIOLATOR_SPEED_READING("Violator Speed Reading"),
	VIOLATOR_DIRECTION("Violator Direction"),
	TRAFFIC_VIOLATOR_SPEED("Traffic Violator Speed"),
	ADMITTED_SPEED("Admitted Speed"),
	P_C_SPEED("P.C. Speed"),
	WEATHER("Weather"),
	VISIBILITY("Visibility"),
	NATURAL_LIGHT("Natural Light"),
	TRAFFIC_CONDITION("Traffic Condition"),
	PEDESTRIAN_TRAFFIC("Pedestrian Traffic"),
	NO_OF_PASSENGERS("No of Passengers"),
	ROAD_TYPE("Road Type"),
	ROAD_SURFACE("Road Surface"),
	ROAD_CONDITION("Road Condition"),
	SPEED_CALCULATED_BY("Speed Calculated By"),
	RADAR_SERIAL_NO("Radar Serial No."),
	RADAR_HEAD_DIRECTION("Radar Head Direction"),
	RADAR_MAKE("Radar Make"),
	RADAR_MODEL("Radar Model"),
	;
	
	private String fieldName;

	public String fieldName() {
		return fieldName;
	}
	
	DataKey(String fieldName) {
		this.fieldName = fieldName;
	}
	
}
