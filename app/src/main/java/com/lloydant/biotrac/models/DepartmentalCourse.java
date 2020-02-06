package com.lloydant.biotrac.models;

import java.util.ArrayList;

public class DepartmentalCourse {
    private String id;
    private String title;
    private String code;
    private int level;
    private String semester;
    private int credit_unit;
    private Department department;
    private ArrayList<Lecturer> assgined_lecturers;

    public DepartmentalCourse(String id, String title, String code, int level,
                              String semester, int credit_unit, Department department, ArrayList<Lecturer> assgined_lecturers) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.level = level;
        this.semester = semester;
        this.credit_unit = credit_unit;
        this.department = department;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getCredit_unit() {
        return credit_unit;
    }

    public void setCredit_unit(int credit_unit) {
        this.credit_unit = credit_unit;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public ArrayList<Lecturer> getAssgined_lecturers() {
        return assgined_lecturers;
    }

    public void setAssgined_lecturers(ArrayList<Lecturer> assgined_lecturers) {
        this.assgined_lecturers = assgined_lecturers;
    }
}
