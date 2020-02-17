package com.lloydant.biotrac.Repositories.implementations;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.lloydant.biotrac.ApolloConnector;
import com.lloydant.biotrac.Repositories.IUpdateFingerprintRepository;
import com.lloydant.biotrac.UpdateLecturerBiometricMutation;
import com.lloydant.biotrac.UpdateStudentBiometricMutation;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UpdateFingerprintRepo implements IUpdateFingerprintRepository {

    @Override
    public Observable<Response<UpdateLecturerBiometricMutation.Data>> UpdateLecturerFingerprint(String reason, int newFinger, int prevFinger, String lecturerId, String template, String token) {
        ApolloCall<UpdateLecturerBiometricMutation.Data> apolloCall = ApolloConnector.setupApollo(token)
                .mutate(new UpdateLecturerBiometricMutation(reason,newFinger,prevFinger,lecturerId,template));

        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Response<UpdateStudentBiometricMutation.Data>> UpdateStudentFingerprint(String reason, int newFinger, int prevFinger, String student, String template, String token) {
        ApolloCall<UpdateStudentBiometricMutation.Data> apolloCall = ApolloConnector.setupApollo(token)
                .mutate(new UpdateStudentBiometricMutation(reason,newFinger,prevFinger,student,template));

        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
