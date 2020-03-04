package com.lloydant.biotrac.Repositories.implementations;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.lloydant.biotrac.ApolloConnector;
import com.lloydant.biotrac.GetLecturersForEnrollmentQuery;
import com.lloydant.biotrac.GetLecturersQuery;
import com.lloydant.biotrac.Repositories.ILecturerSearchRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LecturerSearchRepo implements ILecturerSearchRepository {

    @Inject
    public LecturerSearchRepo() {
    }

    @Override
    public Observable<Response<GetLecturersForEnrollmentQuery.Data>> GetLecturerList(String token) {
        ApolloCall<GetLecturersForEnrollmentQuery.Data> apolloCall = ApolloConnector.setupApollo(token)
                .query(new GetLecturersForEnrollmentQuery());

        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
