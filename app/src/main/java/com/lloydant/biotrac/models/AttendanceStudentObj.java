package com.lloydant.biotrac.models;

public class AttendanceStudentObj {
    private String student;
    private boolean present;
    private String fingerprint;

    public AttendanceStudentObj(String student, boolean present, String fingerprint) {
        this.student = student;
        this.present = present;
        this.fingerprint = fingerprint;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
