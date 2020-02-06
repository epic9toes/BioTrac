package com.lloydant.biotrac;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lloydant.biotrac.presenters.MainActivityPresenter;
import com.lloydant.biotrac.views.MainActivityView;

import androidx.appcompat.app.AppCompatActivity;

import static com.lloydant.biotrac.LoginActivity.USER_PREF;


public class MainActivity extends AppCompatActivity implements MainActivityView {



    private Dialog mBTDeviceDialog;


    private LinearLayout mEnrollStudent, mEnrollLecturer, mStdBioUpdate, mLecturerBioUpdate, mStartAttendance;
    private SharedPreferences mPreferences;
    private View mAdminMenu, mStudentMenu;
    private TextView mUsername;

    private MainActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsername = findViewById(R.id.user_name);

        mBTDeviceDialog = new Dialog(this);
        mBTDeviceDialog.setContentView(R.layout.paired_devices_list);
        mBTDeviceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



        mPresenter = new MainActivityPresenter(this);

       AdminUiMenuSetup();
       StudentUiMenuSetup();
       CheckAdminStatus();

    }

    void CheckAdminStatus(){
//        Boolean isAdmin = getIntent().getExtras().getBoolean("isAdmin");
//        if (isAdmin){
//           mStudentMenu.setVisibility(View.GONE);
//        } else {
//           mAdminMenu.setVisibility(View.GONE);
//        }

        mAdminMenu.setVisibility(View.VISIBLE);
        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        String name = mPreferences.getString("name", "Name Placeholder");
        mUsername.setText(name);
    }

    void AdminUiMenuSetup(){
        mAdminMenu = findViewById(R.id.admin_menu);
        mEnrollLecturer = mAdminMenu.findViewById(R.id.enroll_lecturer);
        mStdBioUpdate = mAdminMenu.findViewById(R.id.update_std_bio);
        mLecturerBioUpdate = mAdminMenu.findViewById(R.id.update_lecturer_bio);

        mEnrollLecturer.setOnClickListener(view -> MainActivity.this.startActivity(new Intent(MainActivity.this,
                LecturerSearchActivity.class)));

        mStdBioUpdate.setOnClickListener(view -> MainActivity.this.startActivity(new Intent(MainActivity.this,
                StudentBioUpdateActivity.class)));

        mLecturerBioUpdate.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,
                LecturerBioUpdateActivity.class)));

    }

    void StudentUiMenuSetup(){
        mStudentMenu = findViewById(R.id.student_menu);
        mEnrollStudent = mStudentMenu.findViewById(R.id.enroll_student);
        mStartAttendance = mStudentMenu.findViewById(R.id.start_attendance);

        mEnrollStudent.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,
                StudentSearchActivity.class)));
        mStartAttendance.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,
                DepartmentalCourseListActivity.class)));

    }



}
