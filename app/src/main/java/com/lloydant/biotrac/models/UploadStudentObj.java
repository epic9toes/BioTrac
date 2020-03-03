package com.lloydant.biotrac.models;

public class UploadStudentObj {
    private String s;
    private boolean p;

    public UploadStudentObj(String s, boolean p) {
        this.s = s;
        this.p = p;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public boolean isP() {
        return p;
    }

    public void setP(boolean p) {
        this.p = p;
    }
}
