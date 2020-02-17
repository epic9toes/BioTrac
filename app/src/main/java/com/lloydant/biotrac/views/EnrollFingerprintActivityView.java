package com.lloydant.biotrac.views;

public interface EnrollFingerprintActivityView {
    void OnLecturerFingerprintUploaded(String message);
    void OnStudentFingerprintUploaded(String message);
    void OnFingerprintUploadFailed();
    void OnFingerprintUploadError(Throwable e);

}
