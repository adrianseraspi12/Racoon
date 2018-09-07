package com.suzei.racoon.model;

import java.util.HashMap;

public class Messages {

    private long timestamp;
    private HashMap<String, Object> seen;
    private String from, type, message;

    public Messages() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, Object> getSeen() {
        return seen;
    }

    public void setSeen(HashMap<String, Object> seen) {
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
