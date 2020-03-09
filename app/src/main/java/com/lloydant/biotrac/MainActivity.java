package com.lloydant.biotrac;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lloydant.biotrac.Repositories.implementations.MainActivityRepo;
import com.lloydant.biotrac.dagger2.BioTracApplication;
import com.lloydant.biotrac.helpers.NetworkCheck;
import com.lloydant.biotrac.helpers.StorageHelper;
import com.lloydant.biotrac.models.Coursemate;
import com.lloydant.biotrac.models.DepartmentalCourse;
import com.lloydant.biotrac.presenters.MainActivityPresenter;
import com.lloydant.biotrac.views.MainActivityView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;



public class MainActivity extends AppCompatActivity implements MainActivityView {

    //dynamic setting of the permission for writing the data into phone memory
    private int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private Dialog mBTDeviceDialog;


    private LinearLayout mEnrollStudent, mEnrollLecturer, mStdBioUpdate, mLecturerBioUpdate, mStartAttendance;
    private View mAdminMenu, mStudentMenu;
    private TextView mUsername;
    private View mLoaderView;
    private Button signOut;
    private ImageView profileImg;

    private MainActivityPresenter mPresenter;

    @Inject
    StorageHelper mStorageHelper;

    @Inject
    NetworkCheck mNetworkCheck;

    @Inject
    SharedPreferences mPreferences;

    @Inject
    MainActivityRepo mMainActivityRepo;

    @Inject
    Picasso mPicasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = findViewById(R.id.user_name);
        signOut = findViewById(R.id.signOut);
        mStudentMenu = findViewById(R.id.student_menu);
        profileImg = findViewById(R.id.profileImg);
        mEnrollStudent = mStudentMenu.findViewById(R.id.enroll_student);
        mStartAttendance = mStudentMenu.findViewById(R.id.start_attendance);
        mAdminMenu = findViewById(R.id.admin_menu);
        mEnrollLecturer = mAdminMenu.findViewById(R.id.enroll_lecturer);
        mStdBioUpdate = mAdminMenu.findViewById(R.id.update_std_bio);
        mLecturerBioUpdate = mAdminMenu.findViewById(R.id.update_lecturer_bio);

        mLoaderView = findViewById(R.id.loading);
        mBTDeviceDialog = new Dialog(this);
        mBTDeviceDialog.setContentView(R.layout.paired_devices_list);
        mBTDeviceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((BioTracApplication) getApplication()).getAppComponent().inject(this);
        mPresenter = new MainActivityPresenter(this, mMainActivityRepo);

        mEnrollStudent.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,
                StudentSearchActivity.class)));
        mStartAttendance.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,
                DepartmentalCourseListActivity.class)));

        mEnrollLecturer.setOnClickListener(view -> MainActivity.this.startActivity(new Intent(MainActivity.this,
                LecturerSearchActivity.class)));

        mStdBioUpdate.setOnClickListener(view -> MainActivity.this.startActivity(new Intent(MainActivity.this,
                StudentBioUpdateActivity.class)));

        mLecturerBioUpdate.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,
                LecturerBioUpdateActivity.class)));


       CheckAdminStatus();

       signOut.setOnClickListener(view -> ClearSharedPreference());

        //checking the permission
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                        REQUEST_PERMISSION_CODE);
            }
        }


    }

    void CheckAdminStatus(){
        String name = mPreferences.getString("name", "Name Placeholder");
        String token = mPreferences.getString("token", "Empty Token");
        String isAdmin = mPreferences.getString("isAdmin", "No Value");
        String image = mPreferences.getString("image","image");
        mUsername.setText(name);


        mPicasso.get()
                .load(image)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(profileImg);

        if (isAdmin.contains("Yes") ){
            mStudentMenu.setVisibility(View.GONE);
        } else if (isAdmin.contains("No") ){
            mAdminMenu.setVisibility(View.GONE);
            if (mNetworkCheck.isNetworkAvailable()){

                mPresenter.GetCourseMates(token);
                mPresenter.GetRegisteredCourses(token);
                mLoaderView.setVisibility(View.VISIBLE);
            }else {
                mLoaderView.setVisibility(View.GONE);
                Toast.makeText(this, "Offline Mode!", Toast.LENGTH_SHORT).show();
            }

        }

    }



    @Override
    public void OnGetCourseMates(ArrayList<Coursemate> coursemates) {
        String studentID = mPreferences.getString("id", "Student ID");
        String jsonString = new Gson().toJson(coursemates);
        mLoaderView.setVisibility(View.GONE);
        Toast.makeText(this, mStorageHelper.saveJsonFile("CourseMates",jsonString,studentID), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void OnGetEmptyCourseMates() {
        mLoaderView.setVisibility(View.GONE);
        Toast.makeText(this, "No Course Mates Found!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnGetRegisteredCourses(ArrayList<DepartmentalCourse> departmentalCourses) {
        String studentID = mPreferences.getString("id", "Student ID");
        String jsonString = new Gson().toJson(departmentalCourses);
        mLoaderView.setVisibility(View.GONE);
        Toast.makeText(this, mStorageHelper.saveJsonFile("RegisteredCourses",jsonString,studentID), Toast.LENGTH_SHORT).show();
//        mStorageHelper.readJsonFile(studentID,"RegisteredCourses.json");
    }

    @Override
    public void OnGetEmptyRegisteredCourses() {
        mLoaderView.setVisibility(View.GONE);
        Toast.makeText(this, "No Registered Courses Found!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnFailure(Throwable e) {
        mLoaderView.setVisibility(View.GONE);
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

    }

    public void ClearSharedPreference(){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
