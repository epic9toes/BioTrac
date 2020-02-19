package com.lloydant.biotrac.views;

public interface AttendanceActivityView {
    void OnAttendanceUploaded(String message);
    void OnUploadAttendanceFailed(String message);
    void OnUploadAttendanceError(Throwable e);

}
