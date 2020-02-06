package com.lloydant.biotrac.presenters;

import android.content.SharedPreferences;

//import com.apollographql.apollo.api.Response;
//import com.lloydant.biotrac.LoginMutation;
import com.lloydant.biotrac.Repositories.ILoginRepository;
//import com.lloydant.biotrac.StudentLoginMutation;
import com.lloydant.biotrac.models.Admin;
import com.lloydant.biotrac.models.Department;
import com.lloydant.biotrac.models.Student;
import com.lloydant.biotrac.views.LoginActivityView;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;


public class LoginActivityPresenter {
    SharedPreferences mPreferences;

    private LoginActivityView mView;
    private ILoginRepository mRepository;

    private CompositeDisposable mDisposable = new CompositeDisposable();

    public LoginActivityPresenter(LoginActivityView view, ILoginRepository repository, SharedPreferences mPeferences) {
        this.mView = view;
        this.mRepository = repository;
        this.mPreferences = mPeferences;
    }


    /**
     * This method checks if credentials match for student and
     * cast to the a Student class object before sending details to persist
     * in SharedPreferences
     * @param username
     * @param password
     */
    public void LoginStudent(String username, String password) {
//        mDisposable.add( mRepository.StudentLogin(username, password)
//                .subscribeWith(new DisposableObserver<Response<StudentLoginMutation.Data>>(){
//
//            @Override
//            public void onNext(Response<StudentLoginMutation.Data> dataResponse) {
//                if (dataResponse.data() != null){
//
//              StudentLoginMutation.Doc doc = dataResponse.data().StudentLogin().doc();
//              String token = dataResponse.data().StudentLogin().token();
//              Department department = new Department(doc.department().id(),doc.department().name());
//              Student student = new Student(doc.id(),doc.name(),doc.phone(),doc.email(),
//                      doc.fingerprint(),doc.image(),doc.reg_no(),doc.level(),department,token);
//                    mView.OnStudentLoginSuccess(student);
//                    StudentCredentials(student);
//                } else {
//                    mView.OnLoginFailure();
//                }
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                mView.OnLoginError(e);
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        }));
    }


    /**
     * This method checks if credentials match for any admin and
     * cast to the Admin class object before sending details to persist
     * in SharedPreferences
     * @param email
     * @param password
     */
    public void LoginAdmin(String email, String password){
//    mDisposable.add(mRepository.AdminLogin(email,password)
//            .subscribeWith(new DisposableObserver<Response<LoginMutation.Data>>() {
//
//        @Override
//        public void onNext(Response<LoginMutation.Data> dataResponse) {
//            if (dataResponse.data() != null){
//
//              LoginMutation.Doc  doc = dataResponse.data().Login().doc();
//              String token  =  dataResponse.data().Login().token();
//              Admin admin = new Admin(doc.id(),doc.name(),doc.email(),token);
//                mView.OnAdminLoginSuccess(admin);
//                AdminCredentials(admin);
//            }else {
//                mView.OnLoginFailure();
//            }
//
//        }
//
//        @Override
//        public void onError(Throwable e) {
//            mView.OnLoginError(e);
//        }
//
//        @Override
//        public void onComplete() {
//
//        }
//    }));
    }


    /**
     * Saves logged in student credentials for references
     * @param student
     */
    public void StudentCredentials(Student student){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("id", student.getId());
        editor.putString("name", student.getName());
        editor.putString("phone", student.getPhone());
        editor.putString("email", student.getEmail());
        editor.putString("fingerprint", student.getFingerprint());
        editor.putString("image", student.getImage());
        editor.putString("regno", student.getReg_no());
        editor.putInt("level", student.getLevel());
        editor.putString("token", student.getToken());
        editor.putString("dept_id", student.getDepartment().getId());
        editor.putString("dept_name", student.getDepartment().getName());
        editor.apply();

    }

    /**
     * Saves logged in admin credentials for references
     * @param admin
     */
    public void AdminCredentials(Admin admin){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString("id", admin.getId());
        editor.putString("name", admin.getName());
        editor.putString("email", admin.getEmail());
        editor.putString("token", admin.getToken());
        editor.apply();
    }

    public void DestroyDisposables(){
        mDisposable.clear();
    }


}
