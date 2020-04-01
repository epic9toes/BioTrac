package com.lloydant.biotrac.views;

import java.io.File;

public interface AttendanceActivityView {
    void OnAttendanceUploaded(String message, File file);
    void OnUploadAttendanceFailed(String message);
    void OnUploadAttendanceError(Throwable e);

}
