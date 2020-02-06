package com.lloydant.biotrac.models;

public class Lecturer {
    private String id;
    private String name;
    private String phone;
    private String email;
    private String fingerprint;

    public Lecturer(String id, String name, String phone, String email, String fingerprint) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.fingerprint = fingerprint;
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
}
