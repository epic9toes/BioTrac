package com.lloydant.biotrac.presenters;


import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetStudentByNoQuery;
import com.lloydant.biotrac.Repositories.implementations.StudentBioUpdateRepo;
import com.lloydant.biotrac.models.Department;
import com.lloydant.biotrac.models.Student;
import com.lloydant.biotrac.views.StudentBioUpdateActivityView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class StudentBioUpdateActivityPresenter {

    private CompositeDisposable mDisposable = new CompositeDisposable();

    private StudentBioUpdateActivityView mView;
    private StudentBioUpdateRepo mRepo;

    public StudentBioUpdateActivityPresenter(StudentBioUpdateActivityView view, StudentBioUpdateRepo repo) {
        mView = view;
        mRepo = repo;
    }

    public void FindStudent(String regNo, String token) {
        mDisposable.add(mRepo.FindStudentByRegNo(regNo,token).subscribeWith(
                new DisposableObserver<Response<GetStudentByNoQuery.Data>>() {
            @Override
            public void onNext(Response<GetStudentByNoQuery.Data> dataResponse) {
                if (dataResponse.data() != null){
                    GetStudentByNoQuery.Doc data = dataResponse.data().GetStudentByNo().doc();
                    Student student = new Student(data.id(),data.name(),data.phone(),data.email(),data.fingerprint(),data.image(),
                            data.reg_no(),data.level(),new Department(data.department().id(),data.department().name()),"");
                    mView.OnGetStudent(student);
                }else mView.OnStudentNotFound();
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
