package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetDepartmentalStudentForEnrollmentQuery;

import io.reactivex.Observable;

public interface IStudentSearchRepository {

    Observable<Response<GetDepartmentalStudentForEnrollmentQuery.Data>> GetStudentsByDepartment(String department, int level, String token);

}
