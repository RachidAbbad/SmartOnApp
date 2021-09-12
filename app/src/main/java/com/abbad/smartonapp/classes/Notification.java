package com.abbad.smartonapp.classes;

public class Notification {
    private String title,body,id,time,gravity,lu,vu;

    public Notification(String title, String body, String id, String time) {
        this.title = title;
        this.body = body;
        this.id = id;
        this.time = time;
    }

    public Notification(){}

    public Notification(String title, String body, String time) {
        this.title = title;
        this.body = body;
        this.time = time;
    }

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getGravity() {
        return gravity;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }

    public String getLu() {
        return lu;
    }

    public void setLu(String lu) {
        this.lu = lu;
    }

    public String getVu() {
        return vu;
    }

    public void setVu(String vu) {
        this.vu = vu;
    }
}
