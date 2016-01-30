package com.hyperiongray.rcmp.extract;

public enum DataKey {

	TICKET_NO("Ticket"),
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
	LOCATION("Location");
	
	private String fieldName;

	public String fieldName() {
		return fieldName;
	}
	
	DataKey(String fieldName) {
		this.fieldName = fieldName;
	}
	
}
