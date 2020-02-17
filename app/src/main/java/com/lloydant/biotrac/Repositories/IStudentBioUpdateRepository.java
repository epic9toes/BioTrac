package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetStudentByNoQuery;

import io.reactivex.Observable;

public interface IStudentBioUpdateRepository {

Observable<Response<GetStudentByNoQuery.Data>> FindStudentByRegNo(String regno, String token);
}
