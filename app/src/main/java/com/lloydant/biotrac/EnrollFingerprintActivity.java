package com.lloydant.biotrac;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.fpcore.FPMatch;
import com.fgtit.reader.BluetoothReaderService;
import com.lloydant.biotrac.Repositories.implementations.EnrollFingerprintRepo;
import com.lloydant.biotrac.presenters.EnrollFingerprintPresenter;
import com.lloydant.biotrac.views.EnrollFingerprintActivityView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.lloydant.biotrac.LecturerSearchActivity.LecturerActivity;
import static com.lloydant.biotrac.LoginActivity.USER_PREF;
import static com.lloydant.biotrac.StudentSearchActivity.StudentActivity;


public class EnrollFingerprintActivity extends AppCompatActivity implements EnrollFingerprintActivityView {

    // Debugging
    public static final String TAG = "BluetoothReader";


    //other image size
    public static final int IMG200 = 200;
    public static final int IMG288 = 288;
    public static final int IMG360 = 360;


    private byte mCmdData[] = new byte[10240];

    private final static byte CMD_GETIMAGE = 0x30;      //GETIMAGE



    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Layout Views
    private ImageView fingerprintImage;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothReaderService mChatService;

    //definition of variables which used for storing the fingerprint template
    public byte mRefData[] = new byte[512]; //enrolled FP template data
    public int mRefSize = 0;


    private final static byte CMD_ENROLHOST = 0x07;    //Enroll to Host

    private int imgSize;


    private byte mDeviceCmd = 0x00;
    private int mCmdSize = 0;
    private boolean mIsWork = false;



    //dynamic setting of the permission for writing the data into phone memory
    private int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private Dialog mEnrollmentSuccessfulDialog, mEnrollmentErrorDialog;
    private Button ConnectBluetoothBtn, onEnrollmentSucess, onEnrollmentError;
    private TextView DeviceName, errorMsg;
    private ImageView backBtn;
    private View StartPanel, BTSearchPanel, deviceInfoPanel, loadingPanel;
    private TextView mUsernameTextView;
    private String name;
    private String token;
    private String lecturerID, studentID;

    private SharedPreferences mPreferences;
    SDKUniversalEndPoints sdkUniversalEndPoints;
    EnrollFingerprintPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_fingerprint);

        backBtn = findViewById(R.id.backBtn);
        DeviceName = findViewById(R.id.deviceName);
        ConnectBluetoothBtn = findViewById(R.id.searchBT);
        StartPanel = findViewById(R.id.startPanel);
        BTSearchPanel = findViewById(R.id.searchBtPanel);
        loadingPanel = findViewById(R.id.loadingPanel);
        deviceInfoPanel = findViewById(R.id.deviceInfoPanel);
        mUsernameTextView = findViewById(R.id.username);

        mEnrollmentSuccessfulDialog = new Dialog(this);
        mEnrollmentSuccessfulDialog.setContentView(R.layout.enroll_success_dialog);
        mEnrollmentSuccessfulDialog.setCanceledOnTouchOutside(false);
        onEnrollmentSucess = mEnrollmentSuccessfulDialog.findViewById(R.id.doneBtn);
        onEnrollmentSucess.setOnClickListener(view -> {
            mEnrollmentSuccessfulDialog.dismiss();
            finish();
        });

        mEnrollmentErrorDialog = new Dialog(this);
        mEnrollmentErrorDialog.setContentView(R.layout.enroll_error_dialog);
        mEnrollmentErrorDialog.setCanceledOnTouchOutside(false);
        onEnrollmentError = mEnrollmentErrorDialog.findViewById(R.id.errorBtn);
        errorMsg = mEnrollmentErrorDialog.findViewById(R.id.errorMsg);
        onEnrollmentError.setOnClickListener(view -> {
            StartPanel.setVisibility(View.VISIBLE);
            mEnrollmentErrorDialog.dismiss();
            SendCommand(CMD_ENROLHOST, null, 0);

        });

        ConnectBluetoothBtn.setOnClickListener(view -> {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(EnrollFingerprintActivity.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        });

        //checking the permission
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                        REQUEST_PERMISSION_CODE);
            }
        }



        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        sdkUniversalEndPoints = new SDKUniversalEndPoints(mBluetoothAdapter, mChatService);

        mPresenter = new EnrollFingerprintPresenter(new EnrollFingerprintRepo(), this);

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //initialize the match function of the fingerprint
        int feedback = FPMatch.getInstance().InitMatch(1, "https://www.hfteco.com/");
        if (feedback == 0) {
            Toast.makeText(this, "Init Match ok", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Init Match failed", Toast.LENGTH_SHORT).show();
        }

        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        if (getIntent().getExtras().getString(LecturerActivity) != null){
            name = getIntent().getExtras().getString("LecturerName", "Lecturer Name");

        }else if (getIntent().getExtras().getString(StudentActivity) != null){
            name = getIntent().getExtras().getString("StudentName", "Student Name");
        }
        token = mPreferences.getString("token", "Empty Token");
        mUsernameTextView.setText("Hello " + name);
    }



    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }


    /**
     * Generate the command package sending via bluetooth
     * @param cmdid command code for different function achieve.
     * @param data the required data need to send to the device
     * @param size the size of the byte[] data
     */
    public void SendCommand(byte cmdid, byte[] data, int size) {
        if (mIsWork) return;

        int sendsize = 9 + size;
        byte[] sendbuf = new byte[sendsize];
        sendbuf[0] = 'F';
        sendbuf[1] = 'T';
        sendbuf[2] = 0;
        sendbuf[3] = 0;
        sendbuf[4] = cmdid;
        sendbuf[5] = (byte) (size);
        sendbuf[6] = (byte) (size >> 8);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                sendbuf[7 + i] = data[i];
            }
        }
        int sum = sdkUniversalEndPoints.calcCheckSum(sendbuf, (7 + size));
        sendbuf[7 + size] = (byte) (sum);
        sendbuf[8 + size] = (byte) (sum >> 8);

        mIsWork = true;
        sdkUniversalEndPoints.TimeOutStart();
        mDeviceCmd = cmdid;
        mCmdSize = 0;
        mChatService.write(sendbuf);

        switch (sendbuf[4]) {
            case CMD_ENROLHOST:
                break;
        }
    }



    // The Handler that gets information back from the BluetoothChatService
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothReaderService.STATE_CONNECTED:
                            BTSearchPanel.setVisibility(View.GONE);
                            StartPanel.setVisibility(View.VISIBLE);
                            deviceInfoPanel.setVisibility(View.VISIBLE);
                            DeviceName.setText(mConnectedDeviceName);
                            SendCommand(CMD_ENROLHOST, null, 0);
                            break;
                        case BluetoothReaderService.STATE_CONNECTING:
                            Toast.makeText(EnrollFingerprintActivity.this, "Trying to connect...", Toast.LENGTH_SHORT).show();
                          break;
                        case BluetoothReaderService.STATE_LISTEN:
                        case BluetoothReaderService.STATE_NONE:
                            BTSearchPanel.setVisibility(View.VISIBLE);
                            loadingPanel.setVisibility(View.GONE);
                            StartPanel.setVisibility(View.GONE);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    if (readBuf.length > 0) {
                        if (readBuf[0] == (byte) 0x1b) {
                            sdkUniversalEndPoints.AddStatusListHex(readBuf, msg.arg1);
                        } else {
                            ReceiveCommand(readBuf, msg.arg1);
                        }
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);

                    Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public synchronized void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothReaderService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        mPresenter.DestroyDisposables();
    }


    /**
     * configure for the UI components
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        fingerprintImage = findViewById(R.id.imageView1);

        mChatService = new BluetoothReaderService(this, mHandler);
        mOutStringBuffer = new StringBuffer("");   // Initialize the buffer for outgoing messages
    }



    /**
     * Received the response from the device
     * @param databuf the data package response from the device
     * @param datasize the size of the data package
     */
    private void ReceiveCommand(byte[] databuf, int datasize) {
        if (mDeviceCmd == CMD_GETIMAGE) { //receiving the image data from the device

           if (sdkUniversalEndPoints.ConvertFingerprintToImage(imgSize,IMG200,IMG288,IMG360,databuf,datasize) != null){
               fingerprintImage.setImageBitmap(sdkUniversalEndPoints.ConvertFingerprintToImage(imgSize,IMG200,IMG288,IMG360,databuf,datasize));
           }

        } else { //other data received from the device
            // append the databuf received into mCmdData.
            sdkUniversalEndPoints.memcpy(mCmdData, mCmdSize, databuf, 0, datasize);
            mCmdSize = mCmdSize + datasize;
            int totalsize = (byte) (mCmdData[5]) + ((mCmdData[6] << 8) & 0xFF00) + 9;
            if (mCmdSize >= totalsize) {
                mCmdSize = 0;
                mIsWork = false;
                sdkUniversalEndPoints.TimeOutStop();

                //parsing the mCmdData
                if ((mCmdData[0] == 'F') && (mCmdData[1] == 'T')) {
                    switch (mCmdData[4]) {

                        case CMD_ENROLHOST: {
                            int size = (byte) (mCmdData[5]) + ((mCmdData[6] << 8) & 0xFF00) - 1;
                            if (mCmdData[7] == 1) {
                              sdkUniversalEndPoints.memcpy(mRefData, 0, mCmdData, 8, size);
                                mRefSize = size;

//                              Encode the byte[] to string base64
                                String encodedBase64 = android.util.Base64.encodeToString(mRefData, android.util.Base64.DEFAULT);

//                            Send to database through graphQl
                                if (getIntent().getExtras().getString(LecturerActivity) != null){
                                    lecturerID = getIntent().getExtras().getString("LecturerID", "Lecturer ID");

//                                  finally, uploading it to the server
                                    mPresenter.UploadLecturerFingerprint(token,lecturerID,encodedBase64);
                                } else if (getIntent().getExtras().getString(StudentActivity) != null){
                                    studentID = getIntent().getExtras().getString("StudentID", "Student ID");

//                                  finally, uploading it to the server
                                    mPresenter.UploadStudentFingerprint(token,studentID,encodedBase64);
                                }

                                loadingPanel.setVisibility(View.VISIBLE);
                                StartPanel.setVisibility(View.GONE);
                            } else
                                mEnrollmentErrorDialog.show();
                        }
                        break;
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "BT not enabled", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void OnLecturerFingerprintUploaded(String message) {
        loadingPanel.setVisibility(View.GONE);
        mEnrollmentSuccessfulDialog.show();
        mEnrollmentErrorDialog.dismiss();
    }

    @Override
    public void OnStudentFingerprintUploaded(String message) {
        loadingPanel.setVisibility(View.GONE);
        mEnrollmentSuccessfulDialog.show();
        mEnrollmentErrorDialog.dismiss();
    }

    @Override
    public void OnFingerprintUploadFailed() {
        errorMsg.setText("Fingerprint Upload not successful!");
        mEnrollmentErrorDialog.show();
    }

    @Override
    public void OnFingerprintUploadError(Throwable e) {
        errorMsg.setText("Error: " + e.getMessage());
        mEnrollmentErrorDialog.show();
    }
}
