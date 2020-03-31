package com.lloydant.biotrac.presenters;

import android.util.Log;

import com.lloydant.biotrac.views.AttendanceActivityView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AttendanceActivityPresenter {

    AttendanceActivityView mView;

    private OkHttpClient okHttpClient;

    public AttendanceActivityPresenter(AttendanceActivityView view, OkHttpClient okHttpClient) {
        mView = view;
        this.okHttpClient = okHttpClient;
    }


    public void UploadAttendanceData(String token, File file, String filepath){
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file",filepath,
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                file))
                .build();
        Request request = new Request.Builder()
                .url("https://unizik-attendance.netlify.com/.netlify/functions/upload-attendance-func")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " +  token)
                .build();

        // Get okhttp3.Call object.
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull okhttp3.Response response) throws IOException {
                if (response.code() == 200){
                    mView.OnAttendanceUploaded("Attendance uploaded successfully!");
                }else mView.OnUploadAttendanceFailed("Attendance upload failed, something went wrong!");
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
              mView.OnUploadAttendanceError(e);
            }
        });
    }

}
