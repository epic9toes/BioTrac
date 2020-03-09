package com.lloydant.biotrac.presenters;

import com.apollographql.apollo.api.Response;
import com.lloydant.biotrac.GetCoursemateQuery;
import com.lloydant.biotrac.GetRegisteredCoursesQuery;
import com.lloydant.biotrac.Repositories.implementations.MainActivityRepo;
import com.lloydant.biotrac.fragment.StudentFragment;
import com.lloydant.biotrac.models.Course;
import com.lloydant.biotrac.models.Coursemate;
import com.lloydant.biotrac.models.Department;
import com.lloydant.biotrac.models.DepartmentalCourse;
import com.lloydant.biotrac.models.Lecturer;
import com.lloydant.biotrac.models.RegisteredCourse;
import com.lloydant.biotrac.models.Session;
import com.lloydant.biotrac.models.Student;
import com.lloydant.biotrac.views.MainActivityView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public void GetCourseMates(String token){
        mDisposable.add(mRepo.GetCourseMates(token).subscribeWith(
                new DisposableObserver<Response<GetCoursemateQuery.Data>>() {
                    @Override
                    public void onNext(Response<GetCoursemateQuery.Data> dataResponse) {
                    if (dataResponse.data() != null){
                        List<GetCoursemateQuery.Doc> doc = dataResponse.data().GetCoursemate().docs();
                        ArrayList<Coursemate> coursemateArrayList = new ArrayList<>();
                        ArrayList<RegisteredCourse> registeredCourses;

                        for (GetCoursemateQuery.Doc coursemate : doc){

                            registeredCourses = new ArrayList<>();
                            for (GetCoursemateQuery.Registered_course registered_course : coursemate.registered_courses()) {
                                registeredCourses.add(new RegisteredCourse(registered_course.id()));
                            }
                            coursemateArrayList.add( new Coursemate(coursemate.id(),coursemate.name(),coursemate.fingerprint(),
                                    coursemate.image(), coursemate.reg_no(), coursemate.level(), new Department(coursemate.department().id(),
                                    coursemate.department().name()),registeredCourses));
                        }
                        mView.OnGetCourseMates(coursemateArrayList);
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

    public void GetRegisteredCourses(String token){
        mDisposable.add(mRepo.GetRegisteredCourses(token).subscribeWith(
                new DisposableObserver<Response<GetRegisteredCoursesQuery.Data>>() {
            @Override
            public void onNext(Response<GetRegisteredCoursesQuery.Data> dataResponse) {
                // course list
                ArrayList<Course> _courses = new ArrayList<>();
                //session object
                Session session = null;

                // total registered course
                int total = 0;
            // student level
                int level = 0;
            if (dataResponse.data() != null){
                List<GetRegisteredCoursesQuery.Doc> docList = dataResponse.data().GetRegisteredCourses().docs();
                ArrayList<DepartmentalCourse> departmentalCourses = new ArrayList<>();

                assert docList != null;
                for (GetRegisteredCoursesQuery.Doc course : docList){
                    // build session object
                    session = new Session(course.session().id(),course.session().semester(), course.session().title());
                    // get total value
                    total = course.total();
                    // get level value
                    level = course.level();
                    _courses = new ArrayList<>();
                    for (GetRegisteredCoursesQuery.Course course1 : Objects.requireNonNull(course.courses())){


                        ArrayList<Lecturer> lecturerArrayList = new ArrayList<>();

                        if (course1.course().assgined_lecturers() != null){
                            for (GetRegisteredCoursesQuery.Assgined_lecturer lecturer : Objects.requireNonNull(course1.course().assgined_lecturers())){

                                lecturerArrayList.add(new Lecturer(lecturer.id(),lecturer.name(),null,
                                        lecturer.email(),lecturer.fingerprint()));
                            }
                        }

                        _courses.add(new Course(course1.course().id(),course1.course().title(),
                                course1.course().code(),course1.course().credit_unit(),course1.course().semester(),
                                lecturerArrayList));

                    }
                }
                departmentalCourses.add(new DepartmentalCourse(session, _courses,total,level)
                           );
                mView.OnGetRegisteredCourses(departmentalCourses);
            }else mView.OnGetEmptyRegisteredCourses();
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
