package com.lloydant.biotrac;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.fgtit.reader.BluetoothReaderService;
import com.lloydant.biotrac.broadcasts.Bluetooth;
import com.lloydant.biotrac.broadcasts.IBluetooth;
import com.lloydant.biotrac.presenters.MainActivityPresenter;
import com.lloydant.biotrac.views.MainActivityView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.lloydant.biotrac.LoginActivity.USER_PREF;


public class MainActivity extends AppCompatActivity implements IBluetooth, MainActivityView {



    private static final int REQUEST_CONNECT_DEVICE = 2;
    private static final int REQUEST_ENABLE_BT = 1;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private Dialog mBTDeviceDialog;
    private  Bluetooth bluetooth;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // Member object for the chat services
//    private BluetoothReaderService mChatService = null;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private LinearLayout mEnrollStudent, mEnrollLecturer, mStdBioUpdate, mLecturerBioUpdate, mStartAttendance;
    private SharedPreferences mPreferences;
    private View mAdminMenu, mStudentMenu;
    private TextView mUsername, deviceName;
    private LinearLayout connectLayout;
    private MainActivityPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsername = findViewById(R.id.user_name);
        connectLayout = findViewById(R.id.connectLayout);
        deviceName = findViewById(R.id.deviceName);

        mBTDeviceDialog = new Dialog(this);
        mBTDeviceDialog.setContentView(R.layout.paired_devices_list);
//        mListView = mBTDeviceDialog.findViewById(R.id.listView);
        mBTDeviceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//      Bluetooth Broadcast Receiver for when Bluetooth actions changes
        bluetooth = new Bluetooth(this);

        // Initialize the BluetoothChatService to perform bluetooth connections
//        mChatService = new BluetoothReaderService(this, mHandler);

        mPresenter = new MainActivityPresenter(this);

       AdminUiMenuSetup();
       StudentUiMenuSetup();
       CheckAdminStatus();
       BluetoothSetup();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetooth.mReceiver, filter);
    }

    // The Handler that gets information back from the BluetoothChatService
//    @SuppressLint("HandlerLeak")
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MESSAGE_STATE_CHANGE:
//                    switch (msg.arg1) {
//                        case BluetoothReaderService.STATE_CONNECTED:
//                            connectLayout.setVisibility(View.VISIBLE);
//                            deviceName.setText(mConnectedDeviceName);
//                            break;
//                        case BluetoothReaderService.STATE_CONNECTING:
//                            deviceName.setText("Connecting...");
//                            break;
//                        case BluetoothReaderService.STATE_LISTEN:
//                        case BluetoothReaderService.STATE_NONE:
//                           deviceName.setText("None");
//                            break;
//
//                    }
//                    break;
//                case MESSAGE_WRITE:
//                    break;
//                case MESSAGE_READ:
//                    byte[] readBuf = (byte[]) msg.obj;
//                    if (readBuf.length > 0) {
//                        if (readBuf[0] == (byte) 0x1b) {
////                            AddStatusListHex(readBuf, msg.arg1);
//                        } else {
////                            ReceiveCommand(readBuf, msg.arg1);
//                        }
//                    }
//                    break;
//                case MESSAGE_DEVICE_NAME:
//                    // save the connected device's name
//                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
//                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
//                    break;
//                case MESSAGE_TOAST:
//                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
//                    PromptDeviceListActivity();
//                    break;
//            }
//        }
//    };

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                BluetoothSetup();
            }
        }

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BluetoothDevice object
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
//                    mChatService.connect(device);
                }
                break;
        }

    }

    void CheckAdminStatus(){
        Boolean isAdmin = getIntent().getExtras().getBoolean("isAdmin");
        if (isAdmin){
           mStudentMenu.setVisibility(View.GONE);
        } else {
           mAdminMenu.setVisibility(View.GONE);
        }
        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        String name = mPreferences.getString("name", "Name Placeholder");
        mUsername.setText(name);
    }

    void AdminUiMenuSetup(){
        mAdminMenu = findViewById(R.id.admin_menu);
        mEnrollLecturer = mAdminMenu.findViewById(R.id.enroll_lecturer);
        mStdBioUpdate = mAdminMenu.findViewById(R.id.update_std_bio);
        mLecturerBioUpdate = mAdminMenu.findViewById(R.id.update_lecturer_bio);

        mEnrollLecturer.setOnClickListener(view -> MainActivity.this.startActivity(new Intent(MainActivity.this,
                LecturerSearchActivity.class)));

        mStdBioUpdate.setOnClickListener(view -> MainActivity.this.startActivity(new Intent(MainActivity.this,
                StudentBioUpdateActivity.class)));

        mLecturerBioUpdate.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,
                LecturerBioUpdateActivity.class)));

    }

    void StudentUiMenuSetup(){
        mStudentMenu = findViewById(R.id.student_menu);
        mEnrollStudent = mStudentMenu.findViewById(R.id.enroll_student);
        mStartAttendance = mStudentMenu.findViewById(R.id.start_attendance);

        mEnrollStudent.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,
                StudentSearchActivity.class)));
        mStartAttendance.setOnClickListener(view -> startActivity(new Intent(MainActivity.this,
                DepartmentalCourseListActivity.class)));

    }

    void BluetoothSetup(){

        if (bluetoothAdapter == null){
            Toast.makeText(this, " Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }
        else {
            PromptDeviceListActivity();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetooth.mReceiver);
    }

    @Override
    public void OnBluetoothTurnedOff() {
        BluetoothSetup();
    }

    @Override
    public void OnBluetoothTurnedOn() {
        PromptDeviceListActivity();
    }

    void PromptDeviceListActivity(){
        Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }



}
