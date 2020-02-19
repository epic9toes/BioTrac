package com.lloydant.biotrac.presenters;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.Repositories.implementations.EnrollFingerprintRepo;
import com.lloydant.biotrac.UpdateSingleLecturerBiometricMutation;
import com.lloydant.biotrac.UpdateSingleStudentBiometricMutation;
import com.lloydant.biotrac.views.EnrollFingerprintActivityView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class EnrollFingerprintPresenter {


    private EnrollFingerprintRepo mRepo;
    private EnrollFingerprintActivityView mView;
    private CompositeDisposable mDisposable = new CompositeDisposable();

    public EnrollFingerprintPresenter(EnrollFingerprintRepo repo, EnrollFingerprintActivityView view) {
        mRepo = repo;
        mView = view;
    }

    public void UploadLecturerFingerprint(String token, String id, String template){
        mDisposable.add(mRepo.UploadLecturerFingerprint(id,template,token).subscribeWith(
                new DisposableObserver<Response<UpdateSingleLecturerBiometricMutation.Data>>() {
                    @Override
                    public void onNext(Response<UpdateSingleLecturerBiometricMutation.Data> dataResponse) {
                        if (dataResponse.data() != null){
                            mView.OnLecturerFingerprintUploaded(dataResponse.data().UpdateSingleLecturerBiometric().message());
                        } else mView.OnFingerprintUploadFailed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.OnFingerprintUploadError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    public void UploadStudentFingerprint(String token, String id, String template){
        mDisposable.add(mRepo.UploadStudentFingerprint(id,template,token).subscribeWith(
                new DisposableObserver<Response<UpdateSingleStudentBiometricMutation.Data>>() {
                    @Override
                    public void onNext(Response<UpdateSingleStudentBiometricMutation.Data> dataResponse) {
                        if (dataResponse.data() != null){
                            mView.OnStudentFingerprintUploaded(dataResponse.data().UpdateSingleStudentBiometric().message());
                        } else mView.OnFingerprintUploadFailed();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.OnFingerprintUploadError(e);
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
