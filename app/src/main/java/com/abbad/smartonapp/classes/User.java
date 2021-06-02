package com.abbad.smartonapp.classes;

import com.google.gson.JsonElement;

public class User {
    String id;
    String name;
    String lastName;
    String token;
    String email;
    String gsm;
    String type;

    public User(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGsm() {
        return gsm;
    }

    public void setGsm(String gsm) {
        this.gsm = gsm;
    }

    public String getType() {
        return type;
    }

    public void setType(int type) {
        if(type == 3)
            this.type = "Technicien";
    }
    public void setCharType(String type) {

            this.type = type;
    }
}
