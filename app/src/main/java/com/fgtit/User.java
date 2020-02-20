package com.fgtit;

public class User {

    private int id;
    private String fingerprint;

    public User(int id, String fingerprint) {
        this.id = id;
        this.fingerprint = fingerprint;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
