package com.lloydant.biotrac.views;

import com.lloydant.biotrac.models.Lecturer;

import java.util.ArrayList;

public interface LecturerSearchActivityView {

    void OnGetLecturers(ArrayList<Lecturer> lecturerArrayList);
    void OnGetEmptyLecturerList();
    void OnFailure(Throwable e);
}
