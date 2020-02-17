package com.lloydant.biotrac.presenters;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetLecturersForEnrollmentQuery;
import com.lloydant.biotrac.Repositories.ILecturerSearchRepository;
import com.lloydant.biotrac.models.Lecturer;
import com.lloydant.biotrac.views.LecturerSearchActivityView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class LecturerSearchActivityPresenter {

    ILecturerSearchRepository mRepository;
    LecturerSearchActivityView mView;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    public LecturerSearchActivityPresenter(LecturerSearchActivityView view, ILecturerSearchRepository repository) {
        mRepository = repository;
        mView = view;
    }

    public void GetLecturers(String token){
        mDisposable.add(mRepository.GetLecturerList(token).subscribeWith(new DisposableObserver<Response<GetLecturersForEnrollmentQuery.Data>>() {
            @Override
            public void onNext(Response<GetLecturersForEnrollmentQuery.Data> dataResponse) {
                if (dataResponse.data() != null){
                    List<GetLecturersForEnrollmentQuery.Doc> data = dataResponse.data().GetLecturersForEnrollment().docs();
                    ArrayList<Lecturer> lecturers = new ArrayList<>();
                    for (GetLecturersForEnrollmentQuery.Doc lecturer : data){
                           Lecturer readLecturer = new Lecturer(lecturer.id(),lecturer.name(),lecturer.phone(),lecturer.email(),lecturer.fingerprint());
                           lecturers.add(readLecturer);
                    }
                    mView.OnGetLecturers(lecturers);
                }else {
                    mView.OnGetNullDataResponse();
                }
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
}
