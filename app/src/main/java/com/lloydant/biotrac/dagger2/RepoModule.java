package com.lloydant.biotrac.dagger2;

import com.lloydant.biotrac.Repositories.IAttendanceRepository;
import com.lloydant.biotrac.Repositories.IEnrollFingerprintRepository;
import com.lloydant.biotrac.Repositories.ILecturerBioUpdateRepository;
import com.lloydant.biotrac.Repositories.ILecturerSearchRepository;
import com.lloydant.biotrac.Repositories.ILoginRepository;
import com.lloydant.biotrac.Repositories.IMainActivityRepository;
import com.lloydant.biotrac.Repositories.IStudentBioUpdateRepository;
import com.lloydant.biotrac.Repositories.IUpdateFingerprintRepository;
import com.lloydant.biotrac.Repositories.implementations.AttendanceRepo;
import com.lloydant.biotrac.Repositories.implementations.EnrollFingerprintRepo;
import com.lloydant.biotrac.Repositories.implementations.LecturerBioUpdateRepo;
import com.lloydant.biotrac.Repositories.implementations.LecturerSearchRepo;
import com.lloydant.biotrac.Repositories.implementations.LoginRepo;
import com.lloydant.biotrac.Repositories.implementations.MainActivityRepo;
import com.lloydant.biotrac.Repositories.implementations.StudentBioUpdateRepo;
import com.lloydant.biotrac.Repositories.implementations.UpdateFingerprintRepo;

import dagger.Module;
import dagger.Provides;

@Module(includes = {NetworkModule.class})
public class RepoModule {

    @BioTracApplicationScope
    @Provides
    public ILoginRepository mILoginRepository(){
        return new LoginRepo();
    }

    @BioTracApplicationScope
    @Provides
    public IMainActivityRepository mIMainActivityRepository(){
        return new MainActivityRepo();
    }

    @BioTracApplicationScope
    @Provides
    public IAttendanceRepository mIAttendanceRepository(){
        return new AttendanceRepo();
    }

    @BioTracApplicationScope
    @Provides
    public IEnrollFingerprintRepository mIEnrollFingerprintRepository(){
        return new EnrollFingerprintRepo();
    }


    @BioTracApplicationScope
    @Provides
    public ILecturerBioUpdateRepository mILecturerBioUpdateRepository(){
        return new LecturerBioUpdateRepo();
    }

    @BioTracApplicationScope
    @Provides
    public ILecturerSearchRepository mILecturerSearchRepository(){
        return new LecturerSearchRepo();
    }

    @BioTracApplicationScope
    @Provides
    public IStudentBioUpdateRepository mIStudentBioUpdateRepository(){
        return new StudentBioUpdateRepo();
    }

    @BioTracApplicationScope
    @Provides
    public IUpdateFingerprintRepository mIUpdateFingerprintRepository(){
        return new UpdateFingerprintRepo();
    }


}
