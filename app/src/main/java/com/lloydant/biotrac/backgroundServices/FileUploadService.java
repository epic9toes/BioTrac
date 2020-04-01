package com.lloydant.biotrac.backgroundServices;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.lloydant.biotrac.dagger2.BioTracApplication;
import com.lloydant.biotrac.helpers.StorageHelper;
import com.lloydant.biotrac.presenters.AttendanceActivityPresenter;
import com.lloydant.biotrac.views.AttendanceActivityView;

import java.io.File;

import javax.inject.Inject;

import okhttp3.OkHttpClient;

public class FileUploadService extends JobIntentService implements AttendanceActivityView {

    @Inject
    StorageHelper mStorageHelper;

    @Inject
    OkHttpClient okHttpClient;

    @Inject
    SharedPreferences mPreferences;

    private static final String TAG = "FileUploadService";
    private static final int JOB_ID = 102;
    String token;
    String ownerFolder = "Attendance";
    AttendanceActivityPresenter presenter;

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d(TAG, "onCreate");

        ((BioTracApplication) getApplication()).getAppComponent().inject(this);

        token = mPreferences.getString("token", "token");
        presenter = new AttendanceActivityPresenter(this, okHttpClient);
    }

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, FileUploadService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
//        Log.d(TAG, "onHandleWork started...");

        boolean fileStatus = intent.getBooleanExtra("fileState", false);

        if (fileStatus){
            File[] files = new File(mStorageHelper.createUserFolder(ownerFolder)).listFiles();
            //If this pathname does not denote a directory, then listFiles() returns null.


            for (File file : files) {
                if (file.isFile()) {
                    String filepath = mStorageHelper.getFilePath(file.getName(),ownerFolder);
                    if (isStopped()) return;
                    presenter.UploadAttendanceData(token,file, filepath);
//                    Log.d(TAG, "onHandleWork: FileName:" + file.getName());
                    SystemClock.sleep(1000);
                }
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onStopCurrentWork() {
//        Log.d(TAG, "onStopCurrentWork");
        return super.onStopCurrentWork();
    }

    @Override
    public void OnAttendanceUploaded(String message, File file) {
//        Log.d(TAG, file.getName() + " File deleted after successful upload!");
        file.delete();
//        Log.d(TAG, "OnAttendanceUploaded");
    }

    @Override
    public void OnUploadAttendanceFailed(String message) {
//        Log.d(TAG, "OnUploadAttendanceFailed");
    }

    @Override
    public void OnUploadAttendanceError(Throwable e) {
//        Log.d(TAG, "OnUploadAttendanceError: " + e.getMessage());
    }
}
