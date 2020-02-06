package com.lloydant.biotrac.views;


import com.lloydant.biotrac.models.Admin;
import com.lloydant.biotrac.models.Student;

public interface LoginActivityView {
    void OnStudentLoginSuccess(Student student);
    void OnAdminLoginSuccess(Admin admin);
    void OnLoginFailure();
    void OnLoginError(Throwable e);

}
