package com.lloydant.biotrac.presenters;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetLecturerByNoQuery;
import com.lloydant.biotrac.Repositories.implementations.LecturerBioUpdateRepo;
import com.lloydant.biotrac.models.Lecturer;
import com.lloydant.biotrac.views.LecturerBioUpdateActivityView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class LecturerBioUpdateActivityPresenter {

    private LecturerBioUpdateActivityView mView;
    private LecturerBioUpdateRepo mRepo;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    public LecturerBioUpdateActivityPresenter(LecturerBioUpdateActivityView view, LecturerBioUpdateRepo repo) {
        mView = view;
        mRepo = repo;
    }

    public void FindLecturer(String No, String token) {
        mDisposable.add(mRepo.FindLecturerByNo(No,token).subscribeWith(
                new DisposableObserver<Response<GetLecturerByNoQuery.Data>>() {
                    @Override
                    public void onNext(Response<GetLecturerByNoQuery.Data> dataResponse) {
                        if (dataResponse.data() != null){
                            GetLecturerByNoQuery.Doc data = dataResponse.data().GetLecturerByNo().doc();
                            Lecturer lecturer = new Lecturer(data.id(), data.name(),data.phone(),data.email(),data.fingerprint());
                            mView.OnGetLecturer(lecturer);
                        }else mView.OnLecturerNotFound();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.OnFailure(e);
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
