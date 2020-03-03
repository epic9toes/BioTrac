package com.lloydant.biotrac.models;

import java.util.ArrayList;

public class AttendanceObj {
    private String d;
    private String l;
    private String dc;
    private ArrayList<UploadStudentObj> ss;

    public AttendanceObj(String d, String l, String dc, ArrayList<UploadStudentObj> ss) {
        this.d = d;
        this.l = l;
        this.dc = dc;
        this.ss = ss;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        this.l = l;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public ArrayList<UploadStudentObj> getSs() {
        return ss;
    }

    public void setSs(ArrayList<UploadStudentObj> ss) {
        this.ss = ss;
    }
}
