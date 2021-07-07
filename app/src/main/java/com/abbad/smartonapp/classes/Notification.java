package com.abbad.smartonapp.classes;

public class Notification {
    private String title,details,id,time;

    public Notification(String title, String details, String id, String time) {
        this.title = title;
        this.details = details;
        this.id = id;
        this.time = time;
    }

    public Notification(String title, String details, String time) {
        this.title = title;
        this.details = details;
        this.time = time;
    }

    public Notification(String title, String details) {
        this.title = title;
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
