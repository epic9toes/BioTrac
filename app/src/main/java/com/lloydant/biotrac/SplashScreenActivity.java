package com.lloydant.biotrac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.lloydant.biotrac.dagger2.BioTracApplication;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;


public class SplashScreenActivity extends AppCompatActivity {

    @Inject
    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BioTracApplication) getApplication()).getAppComponent().inject(this);

        boolean checkIDExists = mPreferences.contains("id");
        if (checkIDExists){
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));

        } else startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        finish();
    }
}
