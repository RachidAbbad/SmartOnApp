package com.abbad.smartonapp.classes;

public class Chaudiere {
    private String id;
    private float t_depart;
    private float t_retour;
    private float t_inertie;
    private float t_fumee;
    private float depression;
    private float luminositee;

    public Chaudiere(String id, float t_depart, float t_retour, float t_inertie, float t_fumee, float depression, float luminositee) {
        this.id = id;
        this.t_depart = t_depart;
        this.t_retour = t_retour;
        this.t_inertie = t_inertie;
        this.t_fumee = t_fumee;
        this.depression = depression;
        this.luminositee = luminositee;
    }

    public Chaudiere(float t_depart, float t_retour, float t_inertie, float t_fumee, float depression, float luminositee) {
        this.t_depart = t_depart;
        this.t_retour = t_retour;
        this.t_inertie = t_inertie;
        this.t_fumee = t_fumee;
        this.depression = depression;
        this.luminositee = luminositee;
    }

    public Chaudiere() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getT_depart() {
        return t_depart;
    }

    public void setT_depart(float t_depart) {
        this.t_depart = t_depart;
    }

    public float getT_retour() {
        return t_retour;
    }

    public void setT_retour(float t_retour) {
        this.t_retour = t_retour;
    }

    public float getT_inertie() {
        return t_inertie;
    }

    public void setT_inertie(float t_inertie) {
        this.t_inertie = t_inertie;
    }

    public float getT_fumee() {
        return t_fumee;
    }

    public void setT_fumee(float t_fumee) {
        this.t_fumee = t_fumee;
    }

    public float getDepression() {
        return depression;
    }

    public void setDepression(float depression) {
        this.depression = depression;
    }

    public float getLuminositee() {
        return luminositee;
    }

    public void setLuminositee(float luminositee) {
        this.luminositee = luminositee;
    }
}
