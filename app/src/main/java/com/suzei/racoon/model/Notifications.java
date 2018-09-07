package com.suzei.racoon.model;

public class Notifications {

    private String role, type, uid, uid_type;
    private boolean seen;
    private long timestamp;

    public Notifications() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid_type() {
        return uid_type;
    }

    public void setUid_type(String uid_type) {
        this.uid_type = uid_type;
    }
}
