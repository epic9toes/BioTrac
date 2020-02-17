package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.UpdateSingleLecturerBiometricMutation;
import com.lloydant.biotrac.UpdateSingleStudentBiometricMutation;

import io.reactivex.Observable;

public interface IEnrollFingerprintRepository {

    Observable<Response<UpdateSingleLecturerBiometricMutation.Data>>
    UploadLecturerFingerprint(String id, String template, String token);

    Observable<Response<UpdateSingleStudentBiometricMutation.Data>>
    UploadStudentFingerprint(String id, String template, String token);
}
