package com.lloydant.biotrac.presenters;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetDepartmentalStudentForEnrollmentQuery;
import com.lloydant.biotrac.Repositories.implementations.StudentSearchRepo;
import com.lloydant.biotrac.models.Department;
import com.lloydant.biotrac.models.Student;
import com.lloydant.biotrac.views.StudentSearchActivityView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class StudentSearchActivityPresenter {

    private StudentSearchActivityView mView;
    private StudentSearchRepo mRepo;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    public StudentSearchActivityPresenter(StudentSearchActivityView view, StudentSearchRepo repo) {
        mView = view;
        mRepo = repo;
    }

    public void GetStudentsByDepartment(String token, String department, int level){
        mDisposable.add(mRepo.GetStudentsByDepartment(department,level,token).subscribeWith(
                new DisposableObserver<Response<GetDepartmentalStudentForEnrollmentQuery.Data>>() {
            @Override
            public void onNext(Response<GetDepartmentalStudentForEnrollmentQuery.Data> dataResponse) {
                if (dataResponse.data().GetDepartmentalStudentForEnrollment().docs() != null){
                    List<GetDepartmentalStudentForEnrollmentQuery.Doc> doc = dataResponse.data().GetDepartmentalStudentForEnrollment().docs();
                    ArrayList<Student> arrayList = new ArrayList<>();
                    for (GetDepartmentalStudentForEnrollmentQuery.Doc student : doc){

                        arrayList.add(new Student(student.id(),student.name(),student.phone(),
                                student.email(),student.fingerprint(),student.image(),student.reg_no(),student.level()
                                , new Department(student.department().id(),student.department().name()), ""));
                    }
                    mView.OnGetStudents(arrayList);
                } else mView.OnGetNullDataResponse();

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
