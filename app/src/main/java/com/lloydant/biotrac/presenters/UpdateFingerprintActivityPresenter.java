package com.lloydant.biotrac.presenters;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.Repositories.implementations.UpdateFingerprintRepo;
import com.lloydant.biotrac.UpdateLecturerBiometricMutation;
import com.lloydant.biotrac.UpdateStudentBiometricMutation;
import com.lloydant.biotrac.views.UpdateFingerprintView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class UpdateFingerprintActivityPresenter {

    private UpdateFingerprintRepo mRepo;
    private UpdateFingerprintView mView;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    public UpdateFingerprintActivityPresenter(UpdateFingerprintRepo repo, UpdateFingerprintView view) {
        mRepo = repo;
        mView = view;
    }

    public void UpdateLecturerFingerprint(String reason, int newFinger, int prevFinger, String lecturerId, String template, String token){
        mDisposable.add(mRepo.UpdateLecturerFingerprint(reason,newFinger,prevFinger,lecturerId,template,token).subscribeWith(
                new DisposableObserver<Response<UpdateLecturerBiometricMutation.Data>>() {
            @Override
            public void onNext(Response<UpdateLecturerBiometricMutation.Data> dataResponse) {
            if (dataResponse.data().UpdateLecturerBiometric().doc() != null){
                mView.OnLecturerFingerprintUpdate(dataResponse.data().UpdateLecturerBiometric().message());
            } else mView.OnFingerprintUpdateFailed();
            }

            @Override
            public void onError(Throwable e) {
            mView.OnFingerprintUpdateError(e);
            }

            @Override
            public void onComplete() {

            }
        }));
    }

    public void UpdateStudentFingerprint(String reason, int newFinger, int prevFinger, String student, String template, String token){
        mDisposable.add(mRepo.UpdateStudentFingerprint(reason,newFinger,prevFinger,student,template,token).subscribeWith(
                new DisposableObserver<Response<UpdateStudentBiometricMutation.Data>>() {
            @Override
            public void onNext(Response<UpdateStudentBiometricMutation.Data> dataResponse) {
                if (dataResponse.data().UpdateStudentBiometric().doc() != null){
                    mView.OnStudentFingerprintUpdate(dataResponse.data().UpdateStudentBiometric().message());
                } else mView.OnFingerprintUpdateFailed();
            }

            @Override
            public void onError(Throwable e) {
                mView.OnFingerprintUpdateError(e);
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
