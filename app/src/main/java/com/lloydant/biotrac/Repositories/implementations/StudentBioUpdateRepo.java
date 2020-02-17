package com.lloydant.biotrac.Repositories.implementations;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.lloydant.biotrac.ApolloConnector;
import com.lloydant.biotrac.GetStudentByNoQuery;
import com.lloydant.biotrac.Repositories.IStudentBioUpdateRepository;
//import com.lloydant.biotrac.UpdateStudentBiometricMutation;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StudentBioUpdateRepo implements IStudentBioUpdateRepository {
//    @Override
//    public Observable<Response<UpdateStudentBiometricMutation.Data>> UpdateStudentFingerprint(File file) {
//        return null;
//    }

    @Override
    public Observable<Response<GetStudentByNoQuery.Data>> FindStudentByRegNo(String regno, String token) {
        ApolloCall<GetStudentByNoQuery.Data> apolloCall = ApolloConnector.setupApollo(token)
                .query(new GetStudentByNoQuery(regno));

        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
