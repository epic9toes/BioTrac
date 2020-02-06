package com.lloydant.biotrac.models;


public class Student {

    private String id;
    private String name;
    private String phone;
    private String email;
    private String fingerprint;
    private String image;
    private String reg_no;
    private int level;
    private Department department;
    private String token;

    public Student(String id, String name, String phone, String email,
                   String fingerprint, String image, String reg_no, int level,
                   Department department, String token) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.fingerprint = fingerprint;
        this.image = image;
        this.reg_no = reg_no;
        this.level = level;
        this.department = department;
        this.token = token;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
