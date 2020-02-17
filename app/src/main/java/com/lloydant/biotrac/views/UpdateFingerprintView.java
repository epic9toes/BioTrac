package com.lloydant.biotrac.views;

public interface UpdateFingerprintView {
    void OnLecturerFingerprintUpdate(String message);
    void OnStudentFingerprintUpdate(String message);
    void OnFingerprintUpdateFailed();
    void OnFingerprintUpdateError(Throwable e);
}
