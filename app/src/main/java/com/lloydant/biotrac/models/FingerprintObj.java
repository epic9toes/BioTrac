package com.lloydant.biotrac.models;

public class FingerprintObj {
    private byte[] hb;
    private boolean isReadOnly;
    private int offset;
    private int _elementSizeShift;
    private int address;
    private int capacity;
    private int limit;
    private int position;

    public FingerprintObj(byte[] hb, boolean isReadOnly, int offset, int _elementSizeShift, int address, int capacity, int limit, int position) {
        this.hb = hb;
        this.isReadOnly = isReadOnly;
        this.offset = offset;
        this._elementSizeShift = _elementSizeShift;
        this.address = address;
        this.capacity = capacity;
        this.limit = limit;
        this.position = position;
    }

    public byte[] getHb() {
        return hb;
    }

    public void setHb() {
        this.hb = new byte[512];
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        isReadOnly = readOnly;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int get_elementSizeShift() {
        return _elementSizeShift;
    }

    public void set_elementSizeShift(int _elementSizeShift) {
        this._elementSizeShift = _elementSizeShift;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
