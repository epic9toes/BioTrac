package com.lloydant.biotrac.presenters;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.Repositories.implementations.AttendanceRepo;
import com.lloydant.biotrac.UploadAttendanceMutation;
import com.lloydant.biotrac.views.AttendanceActivityView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class AttendanceActivityPresenter {

    AttendanceActivityView mView;
    AttendanceRepo mRepo;

    CompositeDisposable mDisposable = new CompositeDisposable();

    public AttendanceActivityPresenter(AttendanceActivityView view, AttendanceRepo repo) {
        mView = view;
        mRepo = repo;
    }

    public void UploadAttendance(String token, String filePath){

        mDisposable.add(mRepo.UploadAttendance(token,filePath).subscribeWith(
                new DisposableObserver<Response<UploadAttendanceMutation.Data>>() {
            @Override
            public void onNext(Response<UploadAttendanceMutation.Data> dataResponse) {
                if (dataResponse.data().UploadAttendance().status() == 200){
                    mView.OnAttendanceUploaded(dataResponse.data().UploadAttendance().message());
                } else mView.OnUploadAttendanceFailed("Upload failed!");
            }

            @Override
            public void onError(Throwable e) {
            mView.OnUploadAttendanceError(e);
            }

            @Override
            public void onComplete() {

            }
        }));
    }


    public void DestroyDisposables(){
        mDisposable.clear();
    }
}
