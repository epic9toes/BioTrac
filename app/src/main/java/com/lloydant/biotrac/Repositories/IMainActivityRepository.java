package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetCoursemateQuery;

import io.reactivex.Observable;

public interface IMainActivityRepository {

    Observable<Response<GetCoursemateQuery.Data>> GetCourseMates(String token);
}
