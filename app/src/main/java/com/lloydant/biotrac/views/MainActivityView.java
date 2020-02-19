package com.lloydant.biotrac.views;

import com.lloydant.biotrac.models.Coursemate;
import com.lloydant.biotrac.models.DepartmentalCourse;

import java.util.ArrayList;

public interface MainActivityView {
    void OnGetCourseMates(ArrayList<Coursemate> coursemates);
    void OnGetEmptyCourseMates();
    void OnGetRegisteredCourses(ArrayList<DepartmentalCourse> departmentalCourses);
    void OnGetEmptyRegisteredCourses();
    void OnFailure(Throwable e);
}
