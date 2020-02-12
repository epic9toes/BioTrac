package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetStudentsByDepartmentQuery;

import io.reactivex.Observable;

public interface IStudentSearchRepository {

    Observable<Response<GetStudentsByDepartmentQuery.Data>> GetStudentsByDepartment(String department, int level, String token);

}
