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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.lloydant.biotrac.Repositories.implementations.UpdateFingerprintRepo;
import com.lloydant.biotrac.dagger2.BioTracApplication;
import com.lloydant.biotrac.helpers.FingerprintConverter;
import com.lloydant.biotrac.presenters.UpdateFingerprintActivityPresenter;
import com.lloydant.biotrac.views.UpdateFingerprintView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import static com.lloydant.biotrac.BluetoothReaderServiceVariables.DEVICE_NAME;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_DEVICE_NAME;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_READ;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_STATE_CHANGE;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_TOAST;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_WRITE;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.TOAST;
import static com.lloydant.biotrac.LecturerSearchActivity.LecturerActivity;
import static com.lloydant.biotrac.StudentSearchActivity.StudentActivity;

public class UpdateFingerprintActivity extends AppCompatActivity implements UpdateFingerprintView {

    Dialog mErrorDialog, mFingerprintDialog, mEnrollmentSuccessfulDialog;
    ImageView backBtn;
    TextView username;
    private ImageView fingerprintImage;

    private Button ConnectBluetoothBtn, onUpdateSuccess, onUpdateError, cancelBtn;

    private View StartPanel, BTSearchPanel,  loadingPanel;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothReaderService mChatService;


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

    private final static byte CMD_GETCHAR = 0x31;       //GETDATA

    public static final String TAG = "BluetoothReader";

    //other image size
    public static final int IMG360 = 360;

    private int imgSize;
    public byte mUpImage[] = new byte[73728]; // image data
    public int mUpImageSize = 0;
    public int mUpImageCount = 0;

    public int mMatSize = 0;
    private byte mCmdData[] = new byte[10240];
    private final static byte CMD_GETIMAGE = 0x30;      //GETIMAGE

    UpdateFingerprintActivityPresenter mPresenter;
    private String token;

    @Inject
    SharedPreferences mPreferences;

    @Inject
    FingerprintConverter mFingerprintConverter;

    @Inject
    UpdateFingerprintRepo mUpdateFingerprintRepo;

//    UpdateFingerModel Objects
    private String UserId, reason, prevFinger, newFinger;

    private TextView errorMsg;


    private Timer mTimerTimeout = null;
    private TimerTask mTaskTimeout = null;
    private Handler mHandlerTimeout;


    private Dialog mEnrollmentErrorDialog;
    private Button onEnrollmentSucess, onEnrollmentError, onCapture, onConfirmCapture;

    public byte mMatData[] = new byte[512];  // match FP template data
    String JsonString; // fingerprint data gotten from the device

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_fingerprint);

        ((BioTracApplication) getApplication()).getAppComponent().inject(this);

        backBtn = findViewById(R.id.backBtn);
        ConnectBluetoothBtn = findViewById(R.id.searchBT);
        StartPanel = findViewById(R.id.startPanel);
        BTSearchPanel = findViewById(R.id.searchBtPanel);
        loadingPanel = findViewById(R.id.loadingPanel);
        username = findViewById(R.id.username);

        mFingerprintDialog = new Dialog(this);
        mFingerprintDialog.setContentView(R.layout.fingerprint_image);
        mFingerprintDialog.setCancelable(false);
        fingerprintImage =  mFingerprintDialog.findViewById(R.id.fingerprint);

        onCapture = mFingerprintDialog.findViewById(R.id.captureBtn);
        onCapture.setOnClickListener(view -> {
            imgSize = IMG360;
            mUpImageSize = 0;
            SendCommand(CMD_GETIMAGE, null, 0);
        });

        onConfirmCapture = mFingerprintDialog.findViewById(R.id.confirmBtn);


        mEnrollmentSuccessfulDialog = new Dialog(this);
        mEnrollmentSuccessfulDialog.setContentView(R.layout.enroll_success_dialog);
        mEnrollmentSuccessfulDialog.setCanceledOnTouchOutside(false);
        onEnrollmentSucess = mEnrollmentSuccessfulDialog.findViewById(R.id.doneBtn);
        onEnrollmentSucess.setOnClickListener(view -> {

            mEnrollmentSuccessfulDialog.dismiss();
            mFingerprintDialog.dismiss();
            finish();
        });

        mEnrollmentErrorDialog = new Dialog(this);
        mEnrollmentErrorDialog.setContentView(R.layout.enroll_error_dialog);
        mEnrollmentErrorDialog.setCanceledOnTouchOutside(false);
        onEnrollmentError = mEnrollmentErrorDialog.findViewById(R.id.errorBtn);
        errorMsg = mEnrollmentErrorDialog.findViewById(R.id.errorMsg);
        onEnrollmentError.setOnClickListener(view -> {
            mEnrollmentErrorDialog.dismiss();
            mFingerprintDialog.dismiss();
            StartPanel.setVisibility(View.VISIBLE);
            SendCommand(CMD_GETIMAGE, null, 0);

        });

        mPresenter = new UpdateFingerprintActivityPresenter(mUpdateFingerprintRepo,this);
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
        if (getIntent().getExtras().getString("LecturerBioUpdateActivity") != null){
            UserId = getIntent().getExtras().getString("lecturerId", "User ID");
        } else if (getIntent().getExtras().getString("StudentBioUpdateActivity") != null){
            UserId = getIntent().getExtras().getString("student", "User ID");
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
                            imgSize = IMG360;
                            mUpImageSize = 0;
                            SendCommand(CMD_GETIMAGE, null, 0);
                            break;
                        case BluetoothReaderService.STATE_CONNECTING:
                            Toast.makeText(UpdateFingerprintActivity.this, "Trying to connect...", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothReaderService.STATE_LISTEN:
                        case BluetoothReaderService.STATE_NONE:
                            BTSearchPanel.setVisibility(View.VISIBLE);
                            StartPanel.setVisibility(View.GONE);
                            mEnrollmentErrorDialog.dismiss();
                            mFingerprintDialog.dismiss();
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    if (readBuf.length > 0) {
                        if (readBuf[0] == (byte) 0x1b) {
                            AddStatusListHex(readBuf, msg.arg1);
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


    private void AddStatusListHex(byte[] data, int size) {
        String text = "";
        for (int i = 0; i < size; i++) {
            text = text + " " + Integer.toHexString(data[i] & 0xFF).toUpperCase() + "  ";
        }
    }


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

    /**
     * method of copying the byte[] data with specific length
     * @param dstbuf byte[] for storing the copied data with specific length
     * @param dstoffset the starting point for storing
     * @param srcbuf the source byte[] used for copying.
     * @param srcoffset the starting point for copying
     * @param size the length required to copy
     */
    private void memcpy(byte[] dstbuf, int dstoffset, byte[] srcbuf, int srcoffset, int size) {
        for (int i = 0; i < size; i++) {
            dstbuf[dstoffset + i] = srcbuf[srcoffset + i];
        }
    }


    /**
     * calculate the check sum of the byte[]
     * @param buffer byte[] required for calculating
     * @param size the size of the byte[]
     * @return the calculated check sum
     */
    private int calcCheckSum(byte[] buffer, int size) {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum = sum + buffer[i];
        }
        return (sum & 0x00ff);
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
//        Log.d(TAG, "setupChat()");

        mChatService = new BluetoothReaderService(this, mHandler);    // Initialize the BluetoothChatService to perform bluetooth connections
        mOutStringBuffer = new StringBuffer("");                    // Initialize the buffer for outgoing messages
    }


    /**
     * stat the timer for counting
     */
    public void TimeOutStart() {
        if (mTimerTimeout != null) {
            return;
        }
        mTimerTimeout = new Timer();
        mHandlerTimeout = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TimeOutStop();
                if (mIsWork) {
                    mIsWork = false;
                    //AddStatusList("Time Out");
                }
                super.handleMessage(msg);
            }
        };
        mTaskTimeout = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandlerTimeout.sendMessage(message);
            }
        };
        mTimerTimeout.schedule(mTaskTimeout, 10000, 10000);
    }

    /**
     * stop the timer
     */
    public void TimeOutStop() {
        if (mTimerTimeout != null) {
            mTimerTimeout.cancel();
            mTimerTimeout = null;
            mTaskTimeout.cancel();
            mTaskTimeout = null;
        }
    }


    /**
     * Generate the command package sending via bluetooth
     * @param cmdid command code for different function achieve.
     * @param data the required data need to send to the device
     * @param size the size of the byte[] data
     */
    private void SendCommand(byte cmdid, byte[] data, int size) {
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
        int sum = calcCheckSum(sendbuf, (7 + size));
        sendbuf[7 + size] = (byte) (sum);
        sendbuf[8 + size] = (byte) (sum >> 8);

        mIsWork = true;
        TimeOutStart();
        mDeviceCmd = cmdid;
        mCmdSize = 0;
        mChatService.write(sendbuf);

        switch (sendbuf[4]) {

            case CMD_GETIMAGE:
                mUpImageSize = 0;
                break;
            case CMD_GETCHAR:
                break;

        }
    }

    private byte[] changeByte(int data) {
        byte b4 = (byte) ((data) >> 24);
        byte b3 = (byte) (((data) << 8) >> 24);
        byte b2 = (byte) (((data) << 16) >> 24);
        byte b1 = (byte) (((data) << 24) >> 24);
        byte[] bytes = {b1, b2, b3, b4};
        return bytes;
    }

    /**
     * generate the image data into Bitmap format
     * @param width width of the image
     * @param height height of the image
     * @param data image data
     * @return bitmap image data
     */
    private byte[] toBmpByte(int width, int height, byte[] data) {
        byte[] buffer = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            int bfType = 0x424d;
            int bfSize = 54 + 1024 + width * height;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            int bfOffBits = 54 + 1024;

            dos.writeShort(bfType);
            dos.write(changeByte(bfSize), 0, 4);
            dos.write(changeByte(bfReserved1), 0, 2);
            dos.write(changeByte(bfReserved2), 0, 2);
            dos.write(changeByte(bfOffBits), 0, 4);

            int biSize = 40;
            int biWidth = width;
            int biHeight = height;
            int biPlanes = 1;
            int biBitcount = 8;
            int biCompression = 0;
            int biSizeImage = width * height;
            int biXPelsPerMeter = 0;
            int biYPelsPerMeter = 0;
            int biClrUsed = 256;
            int biClrImportant = 0;

            dos.write(changeByte(biSize), 0, 4);
            dos.write(changeByte(biWidth), 0, 4);
            dos.write(changeByte(biHeight), 0, 4);
            dos.write(changeByte(biPlanes), 0, 2);
            dos.write(changeByte(biBitcount), 0, 2);
            dos.write(changeByte(biCompression), 0, 4);
            dos.write(changeByte(biSizeImage), 0, 4);
            dos.write(changeByte(biXPelsPerMeter), 0, 4);
            dos.write(changeByte(biYPelsPerMeter), 0, 4);
            dos.write(changeByte(biClrUsed), 0, 4);
            dos.write(changeByte(biClrImportant), 0, 4);

            byte[] palatte = new byte[1024];
            for (int i = 0; i < 256; i++) {
                palatte[i * 4] = (byte) i;
                palatte[i * 4 + 1] = (byte) i;
                palatte[i * 4 + 2] = (byte) i;
                palatte[i * 4 + 3] = 0;
            }
            dos.write(palatte);

            dos.write(data);
            dos.flush();
            buffer = baos.toByteArray();
            dos.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }


    /**
     * generate the fingerprint image
     * @param data image data
     * @param width width of the image
     * @param height height of the image
     * @param offset default setting as 0
     * @return bitmap image data
     */
    public byte[] getFingerprintImage(byte[] data, int width, int height, int offset) {
        if (data == null) {
            return null;
        }
        byte[] imageData = new byte[width * height];
        for (int i = 0; i < (width * height / 2); i++) {
            imageData[i * 2] = (byte) (data[i + offset] & 0xf0);
            imageData[i * 2 + 1] = (byte) (data[i + offset] << 4 & 0xf0);
        }
        byte[] bmpData = toBmpByte(width, height, imageData);
        return bmpData;
    }


    /**
     * Received the response from the device
     * @param databuf the data package response from the device
     * @param datasize the size of the data package
     */
    private void ReceiveCommand(byte[] databuf, int datasize) {
        if (mDeviceCmd == CMD_GETIMAGE) { //receiving the image data from the device
            if (imgSize == IMG360) {   //image size with 256*360
                memcpy(mUpImage, mUpImageSize, databuf, 0, datasize);
                mUpImageSize = mUpImageSize + datasize;

                if (mUpImageSize >= 46080) {
                    byte[] bmpdata = getFingerprintImage(mUpImage, 256, 360, 0/*18*/);
                    Bitmap image = BitmapFactory.decodeByteArray(bmpdata, 0, bmpdata.length);

                    byte[] inpdata = new byte[92160];
                    int inpsize = 92160;
                    System.arraycopy(bmpdata, 1078, inpdata, 0, inpsize);
                    fingerprintImage.setImageBitmap(image);
                    mUpImageSize = 0;
                    mUpImageCount = mUpImageCount + 1;
                    mIsWork = false;

                    SendCommand(CMD_GETCHAR, null, 0);

                } else if (mUpImageSize == 10){
                    mFingerprintDialog.dismiss();
                    mEnrollmentErrorDialog.show();
                }
            }
        } else { //other data received from the device
            // append the databuf received into mCmdData.
            memcpy(mCmdData, mCmdSize, databuf, 0, datasize);
            mCmdSize = mCmdSize + datasize;
            int totalsize = (byte) (mCmdData[5]) + ((mCmdData[6] << 8) & 0xFF00) + 9;
            if (mCmdSize >= totalsize) {
                mCmdSize = 0;
                mIsWork = false;
                TimeOutStop();

                //parsing the mCmdData
                if ((mCmdData[0] == 'F') && (mCmdData[1] == 'T')) {
                    switch (mCmdData[4]) {

                        case CMD_GETCHAR: {
                            int size = (byte) (mCmdData[5]) + ((mCmdData[6] << 8) & 0xFF00) - 1;
                            if (mCmdData[7] == 1) {
                                memcpy(mMatData, 0, mCmdData, 8, size);
                                mMatSize = size;
                                JsonString = mFingerprintConverter.ByteToJsonString(mMatData);

                                onConfirmCapture.setOnClickListener(view -> {

                                    // Send to database through graphQl
                                    if (getIntent().getExtras().getString("LecturerBioUpdateActivity") != null) {

                                        //   finally, uploading it to the server
                                  mPresenter.UpdateLecturerFingerprint(reason,Integer.parseInt(newFinger) ,Integer.parseInt(prevFinger),UserId,JsonString,token);
                                    }
                                    else if (getIntent().getExtras().getString("StudentBioUpdateActivity") != null) {

                                        // finally, uploading it to the server
                                        mPresenter.UpdateStudentFingerprint(reason,Integer.parseInt(newFinger),Integer.parseInt(prevFinger),UserId,JsonString,token);
                                    }
                                    mFingerprintDialog.dismiss();
                                    loadingPanel.setVisibility(View.VISIBLE);
                                    StartPanel.setVisibility(View.GONE);
                                });

                                mEnrollmentErrorDialog.dismiss();
                                mFingerprintDialog.show();
                            } else  mEnrollmentErrorDialog.show();
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
                    // Get the BluetoothDevice object
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
//                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Bluetooth was not enabled, leaving bluetooth chat.", Toast.LENGTH_SHORT).show();
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
        mEnrollmentSuccessfulDialog.show();
    }

    @Override
    public void OnStudentFingerprintUpdate(String message) {
        loadingPanel.setVisibility(View.GONE);
        mEnrollmentSuccessfulDialog.show();
    }

    @Override
    public void OnFingerprintUpdateFailed() {
        errorMsg.setText("Fingerprint Upload not successful!");
        loadingPanel.setVisibility(View.GONE);
        mEnrollmentErrorDialog.show();
    }

    @Override
    public void OnFingerprintUpdateError(Throwable e) {
        errorMsg.setText("Error: " + e.getMessage());
        loadingPanel.setVisibility(View.GONE);
        mEnrollmentErrorDialog.show();
    }

}
