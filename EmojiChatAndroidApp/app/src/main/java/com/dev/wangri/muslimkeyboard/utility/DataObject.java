package com.dev.wangri.muslimkeyboard.utility;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by cloudstream on 5/21/17.
 */

public class DataObject {
    HashMap<String, Object> timestampCreated;

    public DataObject() {
        timestampCreated = new HashMap<>();
        timestampCreated.put("timestamp", ServerValue.TIMESTAMP);
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @Exclude
    public long getTimestampCreatedLong() {
        return (long) timestampCreated.get("timestamp");
    }
}
