package com.lloydant.biotrac.presenters;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetCoursemateQuery;
import com.lloydant.biotrac.Repositories.implementations.MainActivityRepo;
import com.lloydant.biotrac.fragment.StudentFragment;
import com.lloydant.biotrac.models.Department;
import com.lloydant.biotrac.models.Student;
import com.lloydant.biotrac.views.MainActivityView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class MainActivityPresenter {

    MainActivityView mView;
    MainActivityRepo mRepo;

    CompositeDisposable mDisposable = new CompositeDisposable();

    public MainActivityPresenter(MainActivityView view, MainActivityRepo repo) {
        mView = view;
        mRepo = repo;
    }

    public MainActivityPresenter(MainActivityView view) {
        mView = view;
    }

    public void GetCourseMates(String token){
        mDisposable.add(mRepo.GetCourseMates(token).subscribeWith(
                new DisposableObserver<Response<GetCoursemateQuery.Data>>() {
                    @Override
                    public void onNext(Response<GetCoursemateQuery.Data> dataResponse) {
                    List<GetCoursemateQuery.Doc> doc = dataResponse.data().GetCoursemate().docs();

                    if (dataResponse.data().GetCoursemate().docs() != null){
                        ArrayList<Student> arrayList = new ArrayList<>();

                        for (GetCoursemateQuery.Doc student : doc){
                            StudentFragment fragment = student.fragments().studentFragment();
                            arrayList.add(new Student(fragment.id(),fragment.name(),fragment.phone(),
                                    fragment.email(),fragment.fingerprint(),fragment.image(),fragment.reg_no(),fragment.level()
                            , new Department(fragment.department().fragments().departmentFragment().id(),
                                    fragment.department().fragments().departmentFragment().name()), ""));
                        }
                        mView.OnGetCourseMates(arrayList);
                    } else mView.OnGetEmptyCourseMates();
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
