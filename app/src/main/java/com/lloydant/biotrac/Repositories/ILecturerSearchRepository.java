package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetLecturersForEnrollmentQuery;

import io.reactivex.Observable;

public interface ILecturerSearchRepository {

    Observable<Response<GetLecturersForEnrollmentQuery.Data>> GetLecturerList(String token);
}
