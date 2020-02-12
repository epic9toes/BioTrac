package com.lloydant.biotrac.Repositories.implementations;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.lloydant.biotrac.ApolloConnector;
import com.lloydant.biotrac.GetLecturerByNoQuery;
import com.lloydant.biotrac.Repositories.ILecturerBioUpdateRepository;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LecturerBioUpdateRepo implements ILecturerBioUpdateRepository {
    @Override
    public Observable<Response<GetLecturerByNoQuery.Data>> FindLecturerByNo(String no, String token) {
        ApolloCall<GetLecturerByNoQuery.Data> apolloCall = ApolloConnector.setupApollo(token).query(new GetLecturerByNoQuery(no));
        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
