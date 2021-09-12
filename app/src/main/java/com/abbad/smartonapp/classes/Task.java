package com.abbad.smartonapp.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Task implements Parcelable {
    //Used For Report Media:
    private String idIntervention;
    private boolean status;
    private List<File> images;
    private List<File> videos;
    private List<File> audios;
    private List<File> comments;

    //Used to display info in intervention details :
    private int numTache;
    private String titleTache; // zoneTache
    private List<String> listEquipements;
    private List<String> listActions;


    public Task() {
    }

    public Task(String idIntervention) {
        this.idIntervention = idIntervention;
        images = new ArrayList<>();
        videos = new ArrayList<>();
        audios = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public Task(String idIntervention, int numTache, String titleTache, List<String> listEquipements, List<String> listActions) {
        this.idIntervention = idIntervention;
        this.numTache = numTache;
        this.titleTache = titleTache;
        this.listEquipements = listEquipements;
        this.listActions = listActions;
        images = new ArrayList<>();
        videos = new ArrayList<>();
        audios = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public int getNumTache() {
        return numTache;
    }

    public void setNumTache(int numTache) {
        this.numTache = numTache;
    }

    public String getTitleTache() {
        return titleTache;
    }

    public void setTitleTache(String titleTache) {
        this.titleTache = titleTache;
    }

    public List<String> getListEquipements() {
        return listEquipements;
    }

    public void setListEquipements(List<String> listEquipements) {
        this.listEquipements = listEquipements;
    }

    public List<String> getListActions() {
        return listActions;
    }

    public void setListActions(List<String> listActions) {
        this.listActions = listActions;
    }

    protected Task(Parcel in) {
        idIntervention = in.readString();
        status = in.readByte() != 0;
        images = new ArrayList<>();
        videos = new ArrayList<>();
        audios = new ArrayList<>();
        comments = new ArrayList<>();
    }



    public String getIdIntervention() {
        return idIntervention;
    }

    public void setIdIntervention(String idIntervention) {
        this.idIntervention = idIntervention;
    }

    public List<File> getImages() {
        return images;
    }

    public void setImages(List<File> images) {
        this.images = images;
    }

    public List<File> getVideos() {
        return videos;
    }

    public void setVideos(List<File> videos) {
        this.videos = videos;
    }

    public List<File> getAudios() {
        return audios;
    }

    public void setAudios(List<File> audios) {
        this.audios = audios;
    }

    public List<File> getComments() {
        return comments;
    }

    public void setComments(List<File> comments) {
        this.comments = comments;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idIntervention);
        dest.writeByte((byte) (status ? 1 : 0));
        dest.writeInt(numTache);
        dest.writeString(titleTache);
        dest.writeStringList(listEquipements);
        dest.writeStringList(listActions);
    }
}
