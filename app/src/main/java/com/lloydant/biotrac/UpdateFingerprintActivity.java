package com.lloydant.biotrac;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateFingerprintActivity extends AppCompatActivity {

    Dialog mSuccessDialog;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_fingerprint);

        mSuccessDialog = new Dialog(this);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(view -> ShowSuccessDialog());
    }

    private void ShowSuccessDialog() {
        mSuccessDialog.setContentView(R.layout.update_success_dialog);

        mSuccessDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mSuccessDialog.show();
    }
}
