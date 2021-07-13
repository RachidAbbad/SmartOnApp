package com.abbad.smartonapp.classes;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Task implements Parcelable {
    private String idIntervention;
    private boolean status;
    private List<File> images;
    private List<File> videos;
    private List<File> audios;
    private List<File> comments;

    public Task(){}

    public Task(String idIntervention) {
        this.idIntervention = idIntervention;
        images = new ArrayList<>();
        videos = new ArrayList<>();
        audios = new ArrayList<>();
        comments = new ArrayList<>();
    }


    protected Task(Parcel in) {
        idIntervention = in.readString();
        status = in.readByte() != 0;
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
    }
}
