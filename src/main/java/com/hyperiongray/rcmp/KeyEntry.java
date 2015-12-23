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

    public KeyEntry(String personName, String ticketNumber) {
        this.personName = personName;
        this.ticketNumber = ticketNumber;
        this.hashKey = makeHash();
    }

    private String makeHash() {
        return DigestUtils.md5Hex(ticketNumber + personName);
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

}
