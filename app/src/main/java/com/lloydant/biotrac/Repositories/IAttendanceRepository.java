package com.lloydant.biotrac.Repositories;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.UploadAttendanceMutation;

import java.io.File;

import io.reactivex.Observable;
import okhttp3.Call;

public interface IAttendanceRepository {
    Observable<Response<UploadAttendanceMutation.Data>> UploadAttendance(String token, String file);

    Observable<Call> UploadAttendanceData(String token, File file, String filepath);
}
