package com.lloydant.biotrac.helpers;

import com.google.gson.Gson;
import com.lloydant.biotrac.models.FingerprintObj;

import java.nio.ByteBuffer;

import javax.inject.Inject;

public class FingerprintConverter {

    private Gson mGson;

    @Inject
    public FingerprintConverter(Gson gson) {
        mGson = gson;
    }

    public String ByteToJsonString(byte[] mRefData){
        ByteBuffer buffer = ByteBuffer.wrap(mRefData);
        String jsonString = mGson.toJson(buffer);
        return jsonString;
    }

    public byte[] JsonToByteArray(String jsonString){
        FingerprintObj fingerprintObj = mGson.fromJson(jsonString, FingerprintObj.class);
        ByteBuffer buffer1 = ByteBuffer.wrap(fingerprintObj.getHb());
        byte[] arr = new byte[buffer1.capacity()];
        buffer1.get(arr,0,arr.length);
        return arr;
    }
}
