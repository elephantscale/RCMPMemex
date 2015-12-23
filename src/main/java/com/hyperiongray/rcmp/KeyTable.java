/*
 * Extracts private information and stores it in a separate table. Stores the extracted
 * extracted information, together with keys, in a separate file. This approach implements
 * the RCMP privacy requirements.
 */
package com.hyperiongray.rcmp;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mark
 */
public class KeyTable {

    // Singleton
    private static final KeyTable instance = new KeyTable();

    private KeyTable() {
    }

    public static KeyTable getInstance() {
        return instance;
    }
    private final Map <String, KeyEntry> keyMap = new HashMap<>();
    public Map<String, KeyEntry> getKeyTable() {
        return keyMap;
    }
    public void put(KeyEntry keyEntry) {
        getKeyTable().put(keyEntry.getHashKey(), keyEntry);
    }
}
