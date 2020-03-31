package com.lloydant.biotrac.Repositories.implementations;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.FileUpload;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.lloydant.biotrac.ApolloConnector;
import com.lloydant.biotrac.Repositories.IAttendanceRepository;
import com.lloydant.biotrac.UploadAttendanceMutation;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AttendanceRepo implements IAttendanceRepository {

    @Inject
    OkHttpClient okHttpClient;

    @Inject
    public AttendanceRepo(OkHttpClient  okHttpClient) {
        this.okHttpClient  = okHttpClient;
    }

    @Override
    public Observable<Response<UploadAttendanceMutation.Data>> UploadAttendance(String token, String file) {
        UploadAttendanceMutation mutationUpload = UploadAttendanceMutation.builder().file(
                new FileUpload("application/json", new File(file))).build();
        ApolloCall<UploadAttendanceMutation.Data> apolloCall = ApolloConnector.setupApolloFileUpload(token).mutate(mutationUpload);

         return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Call> UploadAttendanceData(String token, File file, String filepath) {
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

        return (Observable<Call>) call;
    }
}
