package com.lloydant.biotrac;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lloydant.biotrac.customAdapters.DeptCourseAdapter;
import com.lloydant.biotrac.models.DepartmentalCourses;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DepartmentalCourseListActivity extends AppCompatActivity implements DeptCourseAdapter.OnDepartmentalCourseListener {

    private ArrayList<DepartmentalCourses> mCourseLists;
    private Dialog mAuthorizeDialog;
    private TextView mErrorMsg;
    private Button mTryAgainButton, mCancelButton;


    RecyclerView mRecyclerView;
    DeptCourseAdapter mDeptCourseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departmental_course_list);
        mAuthorizeDialog = new Dialog(this);
        mCourseLists = new ArrayList<>();
        PopulateDeptCourseList();

        mRecyclerView = findViewById(R.id.recyclerView);

        mDeptCourseAdapter = new DeptCourseAdapter(mCourseLists,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mDeptCourseAdapter);

        mAuthorizeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                DepartmentalCourseListActivity.this.ShowErrorPanelControls();
            }
        });



    }

    @Override
    public void onDepartmentalCourseClick(View view, int position) {
        String name = mCourseLists.get(position).getCode();
        Toast.makeText(this, "onStudentClick: clicked " + name, Toast.LENGTH_SHORT).show();
        ShowSuccessDialog();
//        startActivity(new Intent(LecturerSearchActivity.this, EnrollFingerprintActivity.class));
    }

    void PopulateDeptCourseList(){
       mCourseLists.add(new DepartmentalCourses("MEDLAB101", "Medical Clinics",
               "300 Level", "2nd Semester"));
        mCourseLists.add(new DepartmentalCourses("GST101", "General English Studies",
                "100 Level", "1st Semester"));
        mCourseLists.add(new DepartmentalCourses("GEO204", "Geographical Studies",
                "200 Level", "3rd Semester"));
        mCourseLists.add(new DepartmentalCourses("TECH203", "Technology & Clients",
                "200 Level", "2nd Semester"));
        mCourseLists.add(new DepartmentalCourses("BIOCHEM104", "Biology & Chemistry",
                "100 Level", "3rd Semester"));
        mCourseLists.add(new DepartmentalCourses("GST101", "General English Studies",
                "100 Level", "1st Semester"));
        mCourseLists.add(new DepartmentalCourses("GEO204", "Geographical Studies",
                "200 Level", "3rd Semester"));
        mCourseLists.add(new DepartmentalCourses("TECH203", "Technology & Clients",
                "200 Level", "2nd Semester"));
        mCourseLists.add(new DepartmentalCourses("BIOCHEM104", "Biology & Chemistry",
                "100 Level", "3rd Semester"));
        mCourseLists.add(new DepartmentalCourses("MEDLAB101", "Medical Clinics",
                "300 Level", "2nd Semester"));
        mCourseLists.add(new DepartmentalCourses("GST101", "General English Studies",
                "100 Level", "1st Semester"));
    }

    private void ShowSuccessDialog() {
        mAuthorizeDialog.setContentView(R.layout.authorize_attendance);
        mAuthorizeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mErrorMsg = mAuthorizeDialog.findViewById(R.id.errorMsg);
        mTryAgainButton = mAuthorizeDialog.findViewById(R.id.tryAgainBtn);
        mCancelButton = mAuthorizeDialog.findViewById(R.id.cancelBtn);

        mAuthorizeDialog.show();

    }

    private void ShowErrorPanelControls(){

        mTryAgainButton.setVisibility(View.VISIBLE);
        mTryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DepartmentalCourseListActivity.this.startActivity(new Intent(DepartmentalCourseListActivity.this, AttendanceActivity.class));
                mAuthorizeDialog.dismiss();
            }
        });

        mCancelButton.setVisibility(View.VISIBLE);
        mErrorMsg.setVisibility(View.VISIBLE);
        mAuthorizeDialog.show();
    }


}
