package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.UploadAttendanceMutation;

import io.reactivex.Observable;

public interface IAttendanceRepository {
    Observable<Response<UploadAttendanceMutation.Data>> UploadAttendance(String token, String file);
}
