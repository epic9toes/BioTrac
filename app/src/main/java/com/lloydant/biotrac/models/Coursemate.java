package com.lloydant.biotrac.models;

import java.util.ArrayList;

public class Coursemate {

    private String id;
    private String name;
    private String fingerprint;
    private String image;
    private String reg_no;
    private int level;
    private Department mDepartment;
    private ArrayList<RegisteredCourse> mRegisteredCourses;

    public Coursemate(String id, String name, String fingerprint, String image, String reg_no, int level,
                      Department department, ArrayList<RegisteredCourse> registeredCourses) {
        this.id = id;
        this.name = name;
        this.fingerprint = fingerprint;
        this.image = image;
        this.reg_no = reg_no;
        this.level = level;
        mDepartment = department;
        mRegisteredCourses = registeredCourses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReg_no() {
        return reg_no;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Department getDepartment() {
        return mDepartment;
    }

    public void setDepartment(Department department) {
        mDepartment = department;
    }

    public ArrayList<RegisteredCourse> getRegisteredCourses() {
        return mRegisteredCourses;
    }

    public void setRegisteredCourses(ArrayList<RegisteredCourse> registeredCourses) {
        mRegisteredCourses = registeredCourses;
    }
}
