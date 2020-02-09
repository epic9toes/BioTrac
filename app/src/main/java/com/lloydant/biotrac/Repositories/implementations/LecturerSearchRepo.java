package com.lloydant.biotrac.Repositories.implementations;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.lloydant.biotrac.ApolloConnector;
import com.lloydant.biotrac.GetLecturersQuery;
import com.lloydant.biotrac.Repositories.ILecturerSearchRepository;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LecturerSearchRepo implements ILecturerSearchRepository {

    @Override
    public Observable<Response<GetLecturersQuery.Data>> GetLecturerList(String token) {
        ApolloCall<GetLecturersQuery.Data> apolloCall = ApolloConnector.setupApollo(token)
                .query(new GetLecturersQuery());

        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
