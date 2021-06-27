package com.abbad.smartonapp.classes;

public class Intervention {

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
}
