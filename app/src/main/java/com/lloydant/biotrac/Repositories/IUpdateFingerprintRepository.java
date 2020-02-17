package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.UpdateLecturerBiometricMutation;
import com.lloydant.biotrac.UpdateStudentBiometricMutation;

import io.reactivex.Observable;

public interface IUpdateFingerprintRepository {

    Observable<Response<UpdateLecturerBiometricMutation.Data>>
    UpdateLecturerFingerprint(String reason, int newFinger, int prevFinger,String lecturerId, String template, String token);

    Observable<Response<UpdateStudentBiometricMutation.Data>>
    UpdateStudentFingerprint(String reason, int newFinger, int prevFinger,String student, String template, String token);
}
