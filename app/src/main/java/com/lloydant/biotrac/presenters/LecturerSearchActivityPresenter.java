package com.lloydant.biotrac.presenters;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetLecturersQuery;
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
        mDisposable.add(mRepository.GetLecturerList(token).subscribeWith(new DisposableObserver<Response<GetLecturersQuery.Data>>() {
            @Override
            public void onNext(Response<GetLecturersQuery.Data> dataResponse) {
                if (dataResponse.data() != null){
                    List<GetLecturersQuery.Doc> data = dataResponse.data().GetLecturers().docs();
                    ArrayList<Lecturer> lecturers = new ArrayList<>();
                    for (GetLecturersQuery.Doc lecturer : data){
                           Lecturer readLecturer = new Lecturer(lecturer.id(),lecturer.name(),lecturer.phone(),lecturer.email(),lecturer.fingerprint());
                           lecturers.add(readLecturer);
                    }
                    mView.OnGetLecturers(lecturers);
                }else {
                    mView.OnGetEmptyLecturerList();
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
