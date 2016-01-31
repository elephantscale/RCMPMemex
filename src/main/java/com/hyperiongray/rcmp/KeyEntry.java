/*
 * POJO to store private RMCP ticket info
 */
package com.hyperiongray.rcmp;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author mark
 */
public class KeyEntry {

    private final String personName;
    private final String ticketNumber;
    private final String hashKey;
    private final String officerName;
    private final String offenderName;

    public KeyEntry(String personName, String ticketNumber, String officerName, String offenderName) {
        this.personName = personName;
        this.ticketNumber = ticketNumber;
        this.officerName = officerName;
        this.offenderName = offenderName;
        this.hashKey = makeHash();
    }

    private String makeHash() {
        return DigestUtils.md5Hex(ticketNumber);
    }

    /**
     * @return the personName
     */
    public String getPersonName() {
        return personName;
    }

    /**
     * @return the ticketNumber
     */
    public String getTicketNumber() {
        return ticketNumber;
    }

    /**
     * @return the hashKey
     */
    public String getHashKey() {
        return hashKey;
    }

	public String getOfficerName() {
		return officerName;
	}
	
	public String getOffenderName() {
		return offenderName;
	}

}
