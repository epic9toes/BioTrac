package com.lloydant.biotrac.models;

import java.util.ArrayList;

public class AttendanceObj {
    private String date;
    private String lecturer;
    private String departmentalCourse;
    private String student;
    private ArrayList<AttendanceStudentObj> students;

    public AttendanceObj(String date, String lecturer, String departmentalCourse, String student, ArrayList<AttendanceStudentObj> students) {
        this.date = date;
        this.lecturer = lecturer;
        this.departmentalCourse = departmentalCourse;
        this.student = student;
        this.students = students;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getDepartmentalCourse() {
        return departmentalCourse;
    }

    public void setDepartmentalCourse(String departmentalCourse) {
        this.departmentalCourse = departmentalCourse;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public ArrayList<AttendanceStudentObj> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<AttendanceStudentObj> students) {
        this.students = students;
    }
}
