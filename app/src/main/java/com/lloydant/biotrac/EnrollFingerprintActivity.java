package com.lloydant.biotrac;


import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class EnrollFingerprintActivity extends AppCompatActivity {

    private Button ConnectBluetoothBtn, EnrollFPBtn;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_fingerprint);

    }

}
