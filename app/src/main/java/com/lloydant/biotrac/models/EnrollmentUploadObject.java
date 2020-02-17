package com.lloydant.biotrac.models;

public class EnrollmentUploadObject {
    private String id;
    private String fingerprint;

    public EnrollmentUploadObject(String id, String fingerprint) {
        this.id = id;
        this.fingerprint = fingerprint;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
