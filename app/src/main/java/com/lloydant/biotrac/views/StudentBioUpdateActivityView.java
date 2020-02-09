package com.lloydant.biotrac.views;

import com.lloydant.biotrac.models.Student;

public interface StudentBioUpdateActivityView {
    void OnGetStudent(Student student);
    void OnStudentNotFound();
    void OnFailure(Throwable e);
}
