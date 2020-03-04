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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AttendanceRepo implements IAttendanceRepository {

    @Inject
    public AttendanceRepo() {
    }

    @Override
    public Observable<Response<UploadAttendanceMutation.Data>> UploadAttendance(String token, String file) {
        UploadAttendanceMutation mutationUpload = UploadAttendanceMutation.builder().file(
                new FileUpload("application/json", new File(file))).build();
        ApolloCall<UploadAttendanceMutation.Data> apolloCall = ApolloConnector.setupApollo(token).mutate(mutationUpload);

         return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
