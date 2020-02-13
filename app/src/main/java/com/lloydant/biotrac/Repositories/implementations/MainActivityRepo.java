package com.lloydant.biotrac.Repositories.implementations;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.lloydant.biotrac.ApolloConnector;
import com.lloydant.biotrac.GetCoursemateQuery;
import com.lloydant.biotrac.GetRegisteredCoursesQuery;
import com.lloydant.biotrac.Repositories.IMainActivityRepository;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivityRepo implements IMainActivityRepository {


    @Override
    public Observable<Response<GetCoursemateQuery.Data>> GetCourseMates(String token) {
        ApolloCall<GetCoursemateQuery.Data> apolloCall = ApolloConnector.setupApollo(token)
                .query(new GetCoursemateQuery());
        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Response<GetRegisteredCoursesQuery.Data>> GetRegisteredCourses(String token) {
        ApolloCall<GetRegisteredCoursesQuery.Data> apolloCall = ApolloConnector.setupApollo(token)
                .query(new GetRegisteredCoursesQuery());


        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

}
