package com.abbad.smartonapp.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {
    private String idIntervention;
    private String body;
    private TaskData data;
    private Boolean status;
    private int numTask;

    public Task(String body) {
        this.body = body;
    }

    public Task(String body, TaskData data, Boolean status, int numTask) {
        this.body = body;
        this.data = data;
        this.status = status;
        this.numTask = numTask;
    }

    public Task(String idIntervention, String body, int numTask) {
        this.idIntervention = idIntervention;
        this.body = body;
        this.numTask = numTask;
    }

    public Task(String idIntervention, String body, TaskData data, Boolean status, int numTask) {
        this.idIntervention = idIntervention;
        this.body = body;
        this.data = data;
        this.status = status;
        this.numTask = numTask;
    }

    public Task(String body, TaskData data, int numTask) {
        this.body = body;
        this.data = data;
        this.numTask = numTask;
    }

    protected Task(Parcel in) {
        idIntervention = in.readString();
        body = in.readString();
        byte tmpStatus = in.readByte();
        status = tmpStatus == 0 ? null : tmpStatus == 1;
        numTask = in.readInt();
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public TaskData getData() {
        return data;
    }

    public void setData(TaskData data) {
        this.data = data;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getNumTask() {
        return numTask;
    }

    public void setNumTask(int numTask) {
        this.numTask = numTask;
    }

    public String getIdIntervention() {
        return idIntervention;
    }

    public void setIdIntervention(String idIntervention) {
        this.idIntervention = idIntervention;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idIntervention);
        dest.writeString(body);
        dest.writeByte((byte) (status == null ? 0 : status ? 1 : 2));
        dest.writeInt(numTask);
    }
}
