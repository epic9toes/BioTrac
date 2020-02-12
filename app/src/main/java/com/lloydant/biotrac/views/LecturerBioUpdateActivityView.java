package com.lloydant.biotrac.views;

import com.lloydant.biotrac.models.Lecturer;

public interface LecturerBioUpdateActivityView {
    void OnGetLecturer(Lecturer lecturer);
    void OnLecturerNotFound();
    void OnFailure(Throwable e);
}
