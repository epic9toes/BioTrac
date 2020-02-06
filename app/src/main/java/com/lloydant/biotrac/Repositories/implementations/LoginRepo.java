package com.lloydant.biotrac.Repositories.implementations;


//import com.apollographql.apollo.ApolloCall;
//import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.rx2.Rx2Apollo;
import com.lloydant.biotrac.ApolloConnector;
//import com.lloydant.biotrac.LoginMutation;
import com.lloydant.biotrac.Repositories.ILoginRepository;
//import com.lloydant.biotrac.StudentLoginMutation;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class LoginRepo implements ILoginRepository {

//    @Override
//    public Observable<Response<StudentLoginMutation.Data>> StudentLogin(String username, String password) {
//        ApolloCall<StudentLoginMutation.Data> apolloCall = ApolloConnector.setupApollo("")
//                .mutate(new StudentLoginMutation(username,password));
//        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    @Override
//    public Observable<Response<LoginMutation.Data>> AdminLogin(String email, String password) {
//        ApolloCall<LoginMutation.Data> apolloCall = ApolloConnector.setupApollo("")
//                .mutate(new LoginMutation(email, password));
//        return Rx2Apollo.from(apolloCall).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//

}
