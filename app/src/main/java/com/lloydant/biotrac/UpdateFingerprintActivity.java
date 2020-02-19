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
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.fgtit.utils.DBHelper;
import com.google.gson.Gson;
import com.lloydant.biotrac.Repositories.implementations.UpdateFingerprintRepo;
import com.lloydant.biotrac.helpers.FingerprintConverter;
import com.lloydant.biotrac.models.FingerprintObj;
import com.lloydant.biotrac.presenters.UpdateFingerprintActivityPresenter;
import com.lloydant.biotrac.views.UpdateFingerprintView;

import org.json.JSONArray;
import org.json.JSONException;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.lloydant.biotrac.BluetoothReaderServiceVariables.DEVICE_NAME;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_READ;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_STATE_CHANGE;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_TOAST;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_WRITE;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.TOAST;
import static com.lloydant.biotrac.EnrollFingerprintActivity.IMG200;
import static com.lloydant.biotrac.EnrollFingerprintActivity.IMG288;
import static com.lloydant.biotrac.EnrollFingerprintActivity.IMG360;
import static com.lloydant.biotrac.EnrollFingerprintActivity.MESSAGE_DEVICE_NAME;

public class UpdateFingerprintActivity extends AppCompatActivity implements UpdateFingerprintView {

    Dialog mSuccessDialog, mErrorDialog;
    ImageView backBtn;
    TextView username;

    private TextView DeviceName;
    private Button ConnectBluetoothBtn,  onUpdateSucess, onUpdateError, cancelBtn;

    private View StartPanel, BTSearchPanel, deviceInfoPanel, loadingPanel;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothReaderService mChatService;

    private SDKUniversalEndPoints sdkUniversalEndPoints;

    //dynamic setting of the permission for writing the data into phone memory
    private int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;


    private byte mDeviceCmd = 0x00;
    private int mCmdSize = 0;
    private boolean mIsWork = false;
    private byte mCmdData[] = new byte[10240];

    private final static byte CMD_ENROLHOST = 0x07;    //Enroll to Host
    private final static byte CMD_GETIMAGE = 0x30;      //GETIMAGE

    private int imgSize;

    public static final String TAG = "BluetoothReader";

    // Layout Views
    private ImageView fingerprintImage;

    //definition of variables which used for storing the fingerprint template
    public byte mRefData[] = new byte[512]; //enrolled FP template data
    public int mRefSize = 0;

    UpdateFingerprintActivityPresenter mPresenter;
    private String token;
    public static final String USER_PREF = "com.lloydant.attendance.logged_in_user";
    private SharedPreferences mPreferences;
    private FingerprintConverter mFingerprintConverter;

//    UpdateFingerModel Objects
    private String UserId, reason, prevFinger, newFinger;

    private TextView errorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_fingerprint);

        mSuccessDialog = new Dialog(this);
        mSuccessDialog.setContentView(R.layout.update_success_dialog);
        mSuccessDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mSuccessDialog.setCanceledOnTouchOutside(false);
        onUpdateSucess = mSuccessDialog.findViewById(R.id.doneBtn);
        onUpdateSucess.setOnClickListener(view -> {
            mSuccessDialog.dismiss();
            finish();
        });


        mErrorDialog = new Dialog(this);
        mErrorDialog.setContentView(R.layout.update_error_dialog);
        mErrorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mErrorDialog.setCanceledOnTouchOutside(false);
        cancelBtn = mErrorDialog.findViewById(R.id.cancelBtn);
        errorMsg = mErrorDialog.findViewById(R.id.errorMsg);
        onUpdateError = mErrorDialog.findViewById(R.id.errorBtn);
        onUpdateError.setOnClickListener(view -> {
            loadingPanel.setVisibility(View.GONE);
            StartPanel.setVisibility(View.VISIBLE);
            mErrorDialog.dismiss();
            SendCommand(CMD_ENROLHOST, null, 0);
        });
        cancelBtn.setOnClickListener(view -> finish());

        backBtn = findViewById(R.id.backBtn);
        username = findViewById(R.id.username);
        loadingPanel = findViewById(R.id.loadingPanel);

        DeviceName = findViewById(R.id.deviceName);
        ConnectBluetoothBtn = findViewById(R.id.searchBT);
        StartPanel = findViewById(R.id.startPanel);
        BTSearchPanel = findViewById(R.id.searchBtPanel);
        deviceInfoPanel = findViewById(R.id.deviceInfoPanel);

        mFingerprintConverter = new FingerprintConverter(new Gson());
        mPresenter = new UpdateFingerprintActivityPresenter(new UpdateFingerprintRepo(),this);
        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        token = mPreferences.getString("token", "Empty Token");


        ConnectBluetoothBtn.setOnClickListener(view -> {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(UpdateFingerprintActivity.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        });

        backBtn.setOnClickListener(view -> finish());

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
        sdkUniversalEndPoints.CreateDirectory();
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


//        Prepare FingerUpdateModel objects to be sent to the Database
        reason = getIntent().getExtras().getString("reason", "reason for update");
        newFinger = getIntent().getExtras().getString("newFinger", "New finger");
        prevFinger = getIntent().getExtras().getString("prevFinger", "Old Finger");
        username.setText("Hello " + getIntent().getExtras().getString("Name"));

//        Check the incoming activity and set user id
        if (getIntent().getExtras().getString("StudentBioUpdateActivity") != null){
            UserId = getIntent().getExtras().getString("student", "User ID");
        } else if (getIntent().getExtras().getString("LecturerBioUpdateActivity") != null){
            UserId = getIntent().getExtras().getString("lecturerId", "User ID");
           }

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
                            Toast.makeText(UpdateFingerprintActivity.this, "Trying to connect...", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothReaderService.STATE_LISTEN:
                        case BluetoothReaderService.STATE_NONE:
                            BTSearchPanel.setVisibility(View.VISIBLE);
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

                                String JsonString = mFingerprintConverter.ByteToJsonString(mRefData);


//                                Send to database through graphQl
                                if (getIntent().getExtras().getString("StudentBioUpdateActivity") != null){

                                    //   finally, uploading it to the server
                                mPresenter.UpdateStudentFingerprint(reason,Integer.parseInt(newFinger),Integer.parseInt(prevFinger),UserId,JsonString,token);
                                } else if (getIntent().getExtras().getString("LecturerBioUpdateActivity") != null){

                            //   finally, uploading it to the server
                             mPresenter.UpdateLecturerFingerprint(reason,Integer.parseInt(newFinger),Integer.parseInt(prevFinger),UserId,JsonString,token);
                                }

                                loadingPanel.setVisibility(View.VISIBLE);
                                StartPanel.setVisibility(View.GONE);


                            } else
                                mErrorDialog.show();
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
    public void OnLecturerFingerprintUpdate(String message) {
        loadingPanel.setVisibility(View.GONE);
        mSuccessDialog.show();
        mErrorDialog.dismiss();
    }

    @Override
    public void OnStudentFingerprintUpdate(String message) {
        loadingPanel.setVisibility(View.GONE);
        mSuccessDialog.show();
        mErrorDialog.dismiss();
    }

    @Override
    public void OnFingerprintUpdateFailed() {
        errorMsg.setText("Fingerprint Upload not successful!");
        loadingPanel.setVisibility(View.GONE);
        mErrorDialog.show();
    }

    @Override
    public void OnFingerprintUpdateError(Throwable e) {
        errorMsg.setText("Error: " + e.getMessage());
        loadingPanel.setVisibility(View.GONE);
        mErrorDialog.show();
    }
}
