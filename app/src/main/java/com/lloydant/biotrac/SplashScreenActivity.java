package com.lloydant.biotrac;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import static com.lloydant.biotrac.LoginActivity.USER_PREF;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        boolean checkIDExists = mPreferences.contains("id");
        if (checkIDExists){
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));

        } else startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        finish();
    }
}
