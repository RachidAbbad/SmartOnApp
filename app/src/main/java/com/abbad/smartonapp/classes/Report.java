package com.abbad.smartonapp.classes;

import java.util.ArrayList;
import java.util.List;

public class Report {
    private String idReport;
    private String dateValidation;
    private String nomResponsable;
    private String nomSite;
    private String nomIntervention;
    private String dateIntervention;
    private boolean etat;

    private List<String> imagesList;
    private List<String> commentsList;
    private List<String> videosList;
    private List<String> audiosList;

    private List<Task> listTasks;

    public Report(){
        imagesList = new ArrayList<>();
        commentsList = new ArrayList<>();
        videosList = new ArrayList<>();
        audiosList = new ArrayList<>();
        listTasks = new ArrayList<>();
    }

    public String getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(String dateValidation) {
        this.dateValidation = dateValidation;
    }

    public String getNomResponsable() {
        return nomResponsable;
    }

    public void setNomResponsable(String nomResponsable) {
        this.nomResponsable = nomResponsable;
    }

    public String getNomSite() {
        return nomSite;
    }

    public void setNomSite(String nomSite) {
        this.nomSite = nomSite;
    }

    public String getNomIntervention() {
        return nomIntervention;
    }

    public void setNomIntervention(String nomIntervention) {
        this.nomIntervention = nomIntervention;
    }

    public String getDateIntervention() {
        return dateIntervention;
    }

    public void setDateIntervention(String dateIntervention) {
        this.dateIntervention = dateIntervention;
    }

    public List<String> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<String> imagesList) {
        this.imagesList = imagesList;
    }

    public List<String> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<String> commentsList) {
        this.commentsList = commentsList;
    }

    public List<String> getVideosList() {
        return videosList;
    }

    public void setVideosList(List<String> videosList) {
        this.videosList = videosList;
    }

    public List<String> getAudiosList() {
        return audiosList;
    }

    public void setAudiosList(List<String> audiosList) {
        this.audiosList = audiosList;
    }

    public List<Task> getListTasks() {
        return listTasks;
    }

    public void setListTasks(List<Task> listTasks) {
        this.listTasks = listTasks;
    }

    public boolean getEtat() {
        return etat;
    }

    public void setEtat(boolean etat) {
        this.etat = etat;
    }

    public String getIdReport() {
        return idReport;
    }

    public void setIdReport(String idReport) {
        this.idReport = idReport;
    }
}
