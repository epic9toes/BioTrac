package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetCoursemateQuery;
import com.lloydant.biotrac.GetRegisteredCoursesQuery;

import io.reactivex.Observable;

public interface IMainActivityRepository {

    Observable<Response<GetCoursemateQuery.Data>> GetCourseMates(String token);
    Observable<Response<GetRegisteredCoursesQuery.Data>> GetRegisteredCourses(String token);
}
