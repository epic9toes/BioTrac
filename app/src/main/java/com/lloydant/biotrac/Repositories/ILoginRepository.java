package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.LoginMutation;
import com.lloydant.biotrac.StudentLoginMutation;

import io.reactivex.Observable;

public interface ILoginRepository {

    Observable<Response<StudentLoginMutation.Data>> StudentLogin(String username, String password);

    Observable<Response<LoginMutation.Data>> AdminLogin(String email, String password);
}
