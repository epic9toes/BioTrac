package com.lloydant.biotrac.presenters;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetCoursemateQuery;
import com.lloydant.biotrac.GetStudentsByDepartmentQuery;
import com.lloydant.biotrac.Repositories.implementations.StudentSearchRepo;
import com.lloydant.biotrac.fragment.StudentFragment;
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
                new DisposableObserver<Response<GetStudentsByDepartmentQuery.Data>>() {
            @Override
            public void onNext(Response<GetStudentsByDepartmentQuery.Data> dataResponse) {
                if (dataResponse.data().GetStudentsByDepartment().docs() != null){
                    List<GetStudentsByDepartmentQuery.Doc> doc = dataResponse.data().GetStudentsByDepartment().docs();
                    ArrayList<Student> arrayList = new ArrayList<>();
                    for (GetStudentsByDepartmentQuery.Doc student : doc){
                        StudentFragment fragment = student.fragments().studentFragment();
                        arrayList.add(new Student(fragment.id(),fragment.name(),fragment.phone(),
                                fragment.email(),fragment.fingerprint(),fragment.image(),fragment.reg_no(),fragment.level()
                                , new Department(fragment.department().fragments().departmentFragment().id(),
                                fragment.department().fragments().departmentFragment().name()), ""));
                    }
                    mView.OnGetStudents(arrayList);
                } else mView.OnGetEmptyStudentList();

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
