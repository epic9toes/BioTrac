package com.lloydant.biotrac.models;

import java.util.ArrayList;

public class Course {
    private String id;
    private String title;
    private String code;
    private int credit_unit;
    private String semester;
    private ArrayList<Lecturer> assgined_lecturers;

    public Course(String id, String title, String code, int credit_unit, String semester, ArrayList<Lecturer> assgined_lecturers) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.credit_unit = credit_unit;
        this.semester = semester;
        this.assgined_lecturers = assgined_lecturers;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCredit_unit() {
        return credit_unit;
    }

    public void setCredit_unit(int credit_unit) {
        this.credit_unit = credit_unit;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public ArrayList<Lecturer> getAssgined_lecturers() {
        return assgined_lecturers;
    }

    public void setAssgined_lecturers(ArrayList<Lecturer> assgined_lecturers) {
        this.assgined_lecturers = assgined_lecturers;
    }
}
