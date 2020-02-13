package com.lloydant.biotrac.models;


import java.util.ArrayList;

public class DepartmentalCourse {
    private Session mSession;
    private ArrayList<Course> mCourse;
    private int total;
    private int level;

    public DepartmentalCourse(Session session, ArrayList<Course> course, int total, int level) {
        mSession = session;
        mCourse = course;
        this.total = total;
        this.level = level;
    }

    public Session getSession() {
        return mSession;
    }

    public void setSession(Session session) {
        mSession = session;
    }

    public ArrayList<Course> getCourses() {
        return mCourse;
    }

    public void setCourses(ArrayList<Course> course) {
        mCourse = course;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
