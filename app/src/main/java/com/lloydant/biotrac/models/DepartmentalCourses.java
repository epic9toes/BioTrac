package com.lloydant.biotrac.models;

public class DepartmentalCourses {
    private String Code;
    private String Title;
    private String Level;
    private String Semester;

    public DepartmentalCourses(String code, String title, String level, String semester) {
        Code = code;
        Title = title;
        Level = level;
        Semester = semester;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public String getSemester() {
        return Semester;
    }

    public void setSemester(String semester) {
        Semester = semester;
    }
}
