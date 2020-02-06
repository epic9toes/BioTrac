package com.lloydant.biotrac;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StudentBioUpdateActivity extends AppCompatActivity {

    View includeStudentBio;
    Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_bio_update);
        includeStudentBio = findViewById(R.id.includeStudentBio);
        updateBtn = includeStudentBio.findViewById(R.id.updateBtn);

        updateBtn.setOnClickListener(view -> startActivity(new Intent(StudentBioUpdateActivity.this,
                UpdateFingerprintActivity.class)));
    }
}
