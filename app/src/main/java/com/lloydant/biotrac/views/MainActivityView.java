package com.lloydant.biotrac.views;

import com.lloydant.biotrac.models.DepartmentalCourse;
import com.lloydant.biotrac.models.Student;

import java.util.ArrayList;

public interface MainActivityView {
    void OnGetCourseMates(ArrayList<Student> studentArrayList);
    void OnGetEmptyCourseMates();
    void OnGetRegisteredCourses(ArrayList<DepartmentalCourse> departmentalCourses);
    void OnGetEmptyRegisteredCourses();
    void OnFailure(Throwable e);
}
