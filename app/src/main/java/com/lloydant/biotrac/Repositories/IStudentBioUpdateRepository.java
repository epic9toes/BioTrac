package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetStudentByNoQuery;
import com.lloydant.biotrac.UpdateStudentBiometricMutation;

import java.io.File;

import io.reactivex.Observable;

public interface IStudentBioUpdateRepository {

Observable<Response<UpdateStudentBiometricMutation.Data>> UpdateStudentFingerprint(File file);

Observable<Response<GetStudentByNoQuery.Data>> FindStudentByRegNo(String regno, String token);
}
