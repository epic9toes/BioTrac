package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetLecturerByNoQuery;

import io.reactivex.Observable;

public interface ILecturerBioUpdateRepository {

    Observable<Response<GetLecturerByNoQuery.Data>> FindLecturerByNo(String no, String token);
}
