package com.lloydant.biotrac.models;

public class Session {

    private String id;
    private String semester;
    private String title;

    public Session(String id, String semester, String title) {
        this.id = id;
        this.semester = semester;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
