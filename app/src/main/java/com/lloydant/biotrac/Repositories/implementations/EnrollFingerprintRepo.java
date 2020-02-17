package com.lloydant.biotrac.Repositories.implementations;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.lloydant.biotrac.ApolloConnector;
import com.lloydant.biotrac.Repositories.IEnrollFingerprintRepository;
import com.lloydant.biotrac.UpdateSingleLecturerBiometricMutation;
import com.lloydant.biotrac.UpdateSingleStudentBiometricMutation;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EnrollFingerprintRepo implements IEnrollFingerprintRepository {



    @Override
    public Observable<Response<UpdateSingleLecturerBiometricMutation.Data>> UploadLecturerFingerprint(String id, String template, String token) {
        ApolloCall<UpdateSingleLecturerBiometricMutation.Data> apolloCall = ApolloConnector.setupApollo(token)
                .mutate(new UpdateSingleLecturerBiometricMutation(id,template));

        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Response<UpdateSingleStudentBiometricMutation.Data>> UploadStudentFingerprint(String id, String template, String token) {
        ApolloCall<UpdateSingleStudentBiometricMutation.Data> apolloCall = ApolloConnector.setupApollo(token)
                .mutate(new UpdateSingleStudentBiometricMutation(id,template));

        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
