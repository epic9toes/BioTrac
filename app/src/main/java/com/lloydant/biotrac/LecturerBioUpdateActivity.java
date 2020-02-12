package com.lloydant.biotrac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lloydant.biotrac.Repositories.implementations.LecturerBioUpdateRepo;
import com.lloydant.biotrac.models.Lecturer;
import com.lloydant.biotrac.presenters.LecturerBioUpdateActivityPresenter;
import com.lloydant.biotrac.views.LecturerBioUpdateActivityView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import static com.lloydant.biotrac.LoginActivity.USER_PREF;

public class LecturerBioUpdateActivity extends AppCompatActivity  implements LecturerBioUpdateActivityView {

    public static final String LecturerBioUpdateActivity = "LecturerBioUpdateActivity";

    View includeLecturerBio, mLoaderView, notFoundPanel;
    Button updateBtn, btnSearch;
    AppCompatEditText editTextSearch;
    ImageView closeBtn;

    //    View Resources for when lecturer is found
    AppCompatImageView profile_img;
    TextView username, phoneNo, email;
    AutoCompleteTextView reason;
    EditText prevFinger, newFinger;
    String lecturerID;


    SharedPreferences mPreferences;

    LecturerBioUpdateActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_bio_update);
        includeLecturerBio = findViewById(R.id.includeLecturerBio);
        reason = includeLecturerBio.findViewById(R.id.reason);
        prevFinger = includeLecturerBio.findViewById(R.id.prevFingerIndex);
        newFinger = includeLecturerBio.findViewById(R.id.newFingerIndex);
        updateBtn = includeLecturerBio.findViewById(R.id.updateBtn);
        closeBtn = findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(view -> finish());

        editTextSearch = findViewById(R.id.editTextSearch);
        btnSearch = findViewById(R.id.btnSearch);
        mLoaderView = findViewById(R.id.loading);
        notFoundPanel = findViewById(R.id.dataNotFound);

        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        String token = mPreferences.getString("token", "Token not found!");

        mPresenter = new LecturerBioUpdateActivityPresenter(this,new LecturerBioUpdateRepo());

        btnSearch.setOnClickListener(view -> {
            editTextSearch.onEditorAction(EditorInfo.IME_ACTION_DONE);
            mPresenter.FindLecturer(editTextSearch.getText().toString(), token);
            mLoaderView.setVisibility(View.VISIBLE);
            notFoundPanel.setVisibility(View.GONE);
            includeLecturerBio.setVisibility(View.GONE);
        });

        updateBtn.setOnClickListener(view -> {
            if (reason.getText().length() > 1 && prevFinger.getText().length() > 0 && newFinger.getText().length() > 0){
                Intent intent = new Intent(LecturerBioUpdateActivity.this, UpdateFingerprintActivity.class);
                intent.putExtra("LecturerName", username.getText());
                intent.putExtra("LecturerID", lecturerID);
                intent.putExtra("Reason", reason.getText());
                intent.putExtra("prevFinger", prevFinger.getText());
                intent.putExtra("newFinger", newFinger.getText());
                intent.putExtra(LecturerBioUpdateActivity,LecturerBioUpdateActivity);
                reason.setText("");
                prevFinger.setText("");
                newFinger.setText("");
                editTextSearch.setText("");
                mLoaderView.setVisibility(View.GONE);
                notFoundPanel.setVisibility(View.VISIBLE);
                includeLecturerBio.setVisibility(View.GONE);
                startActivity(intent);
            }else Toast.makeText(this, "All fields are compulsory.", Toast.LENGTH_LONG).show();
        });

    }


    @Override
    public void OnGetLecturer(Lecturer lecturer) {
        //        build returned object
        profile_img = includeLecturerBio.findViewById(R.id.profile_image);
        username  = includeLecturerBio.findViewById(R.id.username);
        phoneNo = includeLecturerBio.findViewById(R.id.phoneNo);
        email = includeLecturerBio.findViewById(R.id.email);

//        profile_img.setImageURI(null);
//        profile_img.setImageURI(Uri.parse(lecturer.getImage()));
        username.setText(lecturer.getName());
        phoneNo.setText(lecturer.getPhone());
        email.setText(lecturer.getEmail());
        lecturerID = lecturer.getId();

        includeLecturerBio.setVisibility(View.VISIBLE);
        mLoaderView.setVisibility(View.GONE);
        notFoundPanel.setVisibility(View.GONE);
    }

    @Override
    public void OnLecturerNotFound() {
        includeLecturerBio.setVisibility(View.GONE);
        mLoaderView.setVisibility(View.GONE);
        notFoundPanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnFailure(Throwable e) {
        includeLecturerBio.setVisibility(View.GONE);
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
