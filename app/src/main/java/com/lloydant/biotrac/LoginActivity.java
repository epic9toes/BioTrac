package com.lloydant.biotrac;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.lloydant.biotrac.Repositories.implementations.LoginRepo;
import com.lloydant.biotrac.helpers.StorageHelper;
import com.lloydant.biotrac.models.Admin;
import com.lloydant.biotrac.models.Student;
import com.lloydant.biotrac.presenters.LoginActivityPresenter;
import com.lloydant.biotrac.views.LoginActivityView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements LoginActivityView {

    private Button mLoginBtn;
    private LoginActivityPresenter mPresenter;
    private AutoCompleteTextView username, password;
    private CheckBox mCheckBox;
    private View mLoaderView;
    private TextView mErrorMsg;
    private SharedPreferences mPreferences;
    public static final String USER_PREF = "com.lloydant.attendance.logged_in_user";

    StorageHelper mStorageHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginBtn = findViewById(R.id.login_btn);
        username = findViewById(R.id.username);
        password = findViewById(R.id.user_password);
        mLoaderView = findViewById(R.id.loading);
        mCheckBox = findViewById(R.id.toggle);
        mErrorMsg = findViewById(R.id.errorMsg);

        mPreferences = getSharedPreferences(USER_PREF, Context.MODE_PRIVATE);
        mPresenter = new LoginActivityPresenter(this, new LoginRepo(), mPreferences);

        mStorageHelper = new StorageHelper(this);

        mLoginBtn.setOnClickListener(view -> {
            if (mCheckBox.isChecked()) {
                mPresenter.LoginAdmin(username.getText().toString().trim(),
                        password.getText().toString().trim());
            } else {
                mPresenter.LoginStudent(username.getText().toString().trim(),
                        password.getText().toString().trim());
            }
            mLoaderView.setVisibility(View.VISIBLE);

        });

    }


    @Override
    public void OnStudentLoginSuccess(Student student) {
        LoggedInSuccess();
        mStorageHelper.createUserFolder(student.getId());
    }

    @Override
    public void OnAdminLoginSuccess(Admin admin) {
        LoggedInSuccess();
    }

    @Override
    public void OnLoginFailure() {
        mLoaderView.setVisibility(View.GONE);
        mErrorMsg.setVisibility(View.VISIBLE);
        mErrorMsg.setText("Username or Password is incorrect");
    }

    @Override
    public void OnLoginError(Throwable e) {
        mLoaderView.setVisibility(View.GONE);
        mErrorMsg.setVisibility(View.VISIBLE);
        mErrorMsg.setText("Error: " + e.getMessage() + ", Please check internet connection.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.DestroyDisposables();
    }

    void LoggedInSuccess(){
        username.setText("");
        password.setText("");
        mErrorMsg.setVisibility(View.GONE);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        mLoaderView.setVisibility(View.GONE);
        finish();
    }
}
