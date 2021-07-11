package com.abbad.smartonapp.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class Intervention implements Parcelable {

    private String title;
    private String date;
    private int gravity;
    private String[] todos;
    private String[] tools;
    private String id;


    public Intervention(String title, String date, int gravity, String[] todos, String[] tools, String id) {
        this.title = title;
        this.date = date;
        this.gravity = gravity;
        this.todos = todos;
        this.tools = tools;
        this.id = id;
    }

    public Intervention(String title, String date, int gravity, String[] todos, String[] tools) {
        this.title = title;
        this.date = date;
        this.gravity = gravity;
        this.todos = todos;
        this.tools = tools;
    }

    public Intervention() {}

    protected Intervention(Parcel in) {
        title = in.readString();
        date = in.readString();
        gravity = in.readInt();
        todos = in.createStringArray();
        tools = in.createStringArray();
        id = in.readString();
    }

    public static final Creator<Intervention> CREATOR = new Creator<Intervention>() {
        @Override
        public Intervention createFromParcel(Parcel in) {
            return new Intervention(in);
        }

        @Override
        public Intervention[] newArray(int size) {
            return new Intervention[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public String[] getTodos() {
        return todos;
    }

    public void setTodos(String[] todos) {
        this.todos = todos;
    }

    public String[] getTools() {
        return tools;
    }

    public void setTools(String[] tools) {
        this.tools = tools;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeInt(gravity);
        dest.writeStringArray(todos);
        dest.writeStringArray(tools);
        dest.writeString(id);
    }
}
