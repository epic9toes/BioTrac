package com.lloydant.biotrac.views;

import com.lloydant.biotrac.models.Student;

import java.util.ArrayList;

public interface StudentSearchActivityView {

    void OnGetStudents(ArrayList<Student> studentArrayList);
    void OnGetEmptyStudentList();
    void OnFailure(Throwable e);
}
