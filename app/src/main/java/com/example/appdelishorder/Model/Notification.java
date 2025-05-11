// Model/Notification.java
package com.example.appdelishorder.Model;

import java.util.Date;

public class Notification {
    private String title;
    private String message;
    private long timestamp;
    private boolean isRead;
    private String type; // order_new, order_status, etc.

    public Notification() {
    }

    public Notification(String title, String message, long timestamp, boolean isRead, String type) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
