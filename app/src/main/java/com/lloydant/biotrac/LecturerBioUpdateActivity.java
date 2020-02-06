package com.lloydant.biotrac;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LecturerBioUpdateActivity extends AppCompatActivity {

    View mIncludeView;
    Button mUpdateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_bio_update);

        mIncludeView = findViewById(R.id.includeLecturerBio);
        mUpdateBtn = mIncludeView.findViewById(R.id.updateBtn);

        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LecturerBioUpdateActivity.this.startActivity(new Intent(LecturerBioUpdateActivity.this, UpdateFingerprintActivity.class));
            }
        });
    }
}
