package com.lloydant.biotrac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lloydant.biotrac.Repositories.implementations.StudentBioUpdateRepo;
import com.lloydant.biotrac.models.Student;
import com.lloydant.biotrac.presenters.StudentBioUpdateActivityPresenter;
import com.lloydant.biotrac.views.StudentBioUpdateActivityView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import static com.lloydant.biotrac.LoginActivity.USER_PREF;

public class StudentBioUpdateActivity extends AppCompatActivity implements StudentBioUpdateActivityView {

    public static final String StudentBioUpdateActivity = "StudentBioUpdateActivity";

    View includeStudentBio, mLoaderView, notFoundPanel;
    Button updateBtn, btnSearch;
    AppCompatEditText editTextSearch;
    ImageView closeBtn;

//    View Resources for when student is found
    AppCompatImageView profile_img;
    TextView username, department, regNo;
    AutoCompleteTextView reason;
    EditText prevFinger, newFinger;
    String studentID;


    SharedPreferences mPreferences;

    StudentBioUpdateActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_bio_update);
        includeStudentBio = findViewById(R.id.includeStudentBio);
        reason = includeStudentBio.findViewById(R.id.reason);
        prevFinger = includeStudentBio.findViewById(R.id.prevFingerIndex);
        newFinger = includeStudentBio.findViewById(R.id.newFingerIndex);
        updateBtn = includeStudentBio.findViewById(R.id.updateBtn);
        closeBtn = findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(view -> finish());

        editTextSearch = findViewById(R.id.editTextSearch);
        btnSearch = findViewById(R.id.btnSearch);
        mLoaderView = findViewById(R.id.loading);
        notFoundPanel = findViewById(R.id.dataNotFound);

        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        String token = mPreferences.getString("token", "Token not found!");

        mPresenter = new StudentBioUpdateActivityPresenter(this,new StudentBioUpdateRepo());

        btnSearch.setOnClickListener(view -> {
            editTextSearch.onEditorAction(EditorInfo.IME_ACTION_DONE);
            mPresenter.FindStudent(editTextSearch.getText().toString(), token);
            mLoaderView.setVisibility(View.VISIBLE);
            notFoundPanel.setVisibility(View.GONE);
            includeStudentBio.setVisibility(View.GONE);
        });

        updateBtn.setOnClickListener(view -> {
            if (reason.getText().length() > 1 && prevFinger.getText().length() > 0 && newFinger.getText().length() > 0){
                Intent intent = new Intent(StudentBioUpdateActivity.this, UpdateFingerprintActivity.class);
                intent.putExtra("StudentName", username.getText());
                intent.putExtra("StudentID", studentID);
                intent.putExtra("Reason", reason.getText());
                intent.putExtra("prevFinger", prevFinger.getText());
                intent.putExtra("newFinger", newFinger.getText());
                intent.putExtra(StudentBioUpdateActivity,StudentBioUpdateActivity);
                reason.setText("");
                prevFinger.setText("");
                newFinger.setText("");
                editTextSearch.setText("");
                mLoaderView.setVisibility(View.GONE);
                notFoundPanel.setVisibility(View.VISIBLE);
                includeStudentBio.setVisibility(View.GONE);
                startActivity(intent);
            }else Toast.makeText(this, "All fields are compulsory.", Toast.LENGTH_LONG).show();
        });

    }

    @Override
    public void OnGetStudent(Student student) {
//        build returned object
        profile_img = includeStudentBio.findViewById(R.id.profile_image);
        username  = includeStudentBio.findViewById(R.id.username);
        department = includeStudentBio.findViewById(R.id.department);
        regNo = includeStudentBio.findViewById(R.id.regNo);

        profile_img.setImageURI(null);
        profile_img.setImageURI(Uri.parse(student.getImage()));
        username.setText(student.getName());
        department.setText(student.getDepartment().getName());
        regNo.setText(student.getReg_no());
        studentID = student.getId();

        includeStudentBio.setVisibility(View.VISIBLE);
        mLoaderView.setVisibility(View.GONE);
        notFoundPanel.setVisibility(View.GONE);
    }

    @Override
    public void OnStudentNotFound() {
        includeStudentBio.setVisibility(View.GONE);
        mLoaderView.setVisibility(View.GONE);
        notFoundPanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnFailure(Throwable e) {
        includeStudentBio.setVisibility(View.GONE);
        mLoaderView.setVisibility(View.GONE);
        notFoundPanel.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.DestroyDisposables();
    }
}
