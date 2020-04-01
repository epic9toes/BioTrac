package com.lloydant.biotrac.dagger2;

import com.lloydant.biotrac.ApolloConnector;
import com.lloydant.biotrac.AttendanceActivity;
import com.lloydant.biotrac.DepartmentalCourseListActivity;
import com.lloydant.biotrac.EnrollFingerprintActivity;
import com.lloydant.biotrac.LecturerBioUpdateActivity;
import com.lloydant.biotrac.LecturerSearchActivity;
import com.lloydant.biotrac.LoginActivity;
import com.lloydant.biotrac.MainActivity;
import com.lloydant.biotrac.Repositories.implementations.AttendanceRepo;
import com.lloydant.biotrac.Repositories.implementations.EnrollFingerprintRepo;
import com.lloydant.biotrac.Repositories.implementations.LecturerBioUpdateRepo;
import com.lloydant.biotrac.Repositories.implementations.LecturerSearchRepo;
import com.lloydant.biotrac.Repositories.implementations.LoginRepo;
import com.lloydant.biotrac.Repositories.implementations.MainActivityRepo;
import com.lloydant.biotrac.Repositories.implementations.StudentBioUpdateRepo;
import com.lloydant.biotrac.Repositories.implementations.StudentSearchRepo;
import com.lloydant.biotrac.Repositories.implementations.UpdateFingerprintRepo;
import com.lloydant.biotrac.SplashScreenActivity;
import com.lloydant.biotrac.StudentBioUpdateActivity;
import com.lloydant.biotrac.StudentSearchActivity;
import com.lloydant.biotrac.UpdateFingerprintActivity;
import com.lloydant.biotrac.backgroundServices.FileUploadService;

import dagger.Component;

@BioTracApplicationScope
@Component(modules = {AppModule.class, HelperModule.class})
public interface BioTracApplicationComponent {

//    Injected Classes
    void inject(BioTracApplication bioTracApplication);

//    Injected services
    void inject(FileUploadService fileUploadService);

//    Injected activities
    void inject(LoginActivity loginActivity);

    void inject(StudentBioUpdateActivity studentBioUpdateActivity);

    void inject(SplashScreenActivity splashScreenActivity);

    void inject(MainActivity mainActivity);

    void inject(AttendanceActivity attendanceActivity);

    void inject(DepartmentalCourseListActivity departmentalCourseListActivity);

    void inject(EnrollFingerprintActivity enrollFingerprintActivity);

    void inject(LecturerBioUpdateActivity lecturerBioUpdateActivity);

    void inject(LecturerSearchActivity lecturerSearchActivity);

    void inject(StudentSearchActivity studentSearchActivity);

    void inject(UpdateFingerprintActivity updateFingerprintActivity);

//    Injected repositories
    void inject(LoginRepo loginRepo);

    void inject(AttendanceRepo attendanceRepo);

    void inject(StudentBioUpdateRepo studentBioUpdateRepo);

    void inject(EnrollFingerprintRepo enrollFingerprintRepo);

    void inject(LecturerBioUpdateRepo lecturerBioUpdateRepo);

    void inject(LecturerSearchRepo lecturerSearchRepo);

    void inject(MainActivityRepo mainActivityRepo);

    void inject(StudentSearchRepo studentSearchRepo);

    void inject(UpdateFingerprintRepo updateFingerprintRepo);

}
