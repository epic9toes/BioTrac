package com.lloydant.biotrac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.fpcore.FPMatch;
import com.fgtit.reader.BluetoothReaderService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lloydant.biotrac.Repositories.implementations.AttendanceRepo;
import com.lloydant.biotrac.helpers.FingerprintConverter;
import com.lloydant.biotrac.helpers.NetworkCheck;
import com.lloydant.biotrac.helpers.StorageHelper;
import com.lloydant.biotrac.models.AttendanceObj;
import com.lloydant.biotrac.models.AttendanceStudentObj;
import com.lloydant.biotrac.models.Coursemate;
import com.lloydant.biotrac.models.RegisteredCourse;
import com.lloydant.biotrac.presenters.AttendanceActivityPresenter;
import com.lloydant.biotrac.views.AttendanceActivityView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.lloydant.biotrac.MainActivity.USER_PREF;

public class AttendanceActivity extends AppCompatActivity  implements AttendanceActivityView {

    private TextView username, courseCode, courseTitle, dateTime, timeTxt;
    private String LecturerID, departmentalCourse, studentID, token, LecturerFingerprint;
    private CardView passedStudent;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothReaderService mChatService;


    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;


    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;


    //dynamic setting of the permission for writing the data into phone memory
    private int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private byte mDeviceCmd = 0x00;
    private int mCmdSize = 0;
    private boolean mIsWork = false;

    private byte mCmdData[] = new byte[10240];

    private final static byte CMD_GETIMAGE = 0x30;      //GETIMAGE
    private final static byte CMD_CAPTUREHOST = 0x08;    //Caputre to Host

    private View fingerPanel, searchBtPanel, startAttendancePanel;
    private Button searchBT, btnStartAttendance, finishAttendanceBtn;


    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Debugging
    public static final String TAG = "BluetoothReader";

    private int imgSize;

    //other image size
    public static final int IMG200 = 200;
    public static final int IMG288 = 288;
    public static final int IMG360 = 360;

    //definition of variables which used for storing the fingerprint template
    public byte mMatData[] = new byte[512];  // match FP template
    public int mMatSize = 0;

    private SharedPreferences mPreferences;
    private StorageHelper mStorageHelper;
    private ArrayList<AttendanceStudentObj> mAttendanceStudentObjs = new ArrayList<>();
    private ArrayList<Coursemate> coursemates =  new ArrayList<>();


    private Dialog mAuthorizeDialog;
    private TextView mErrorMsg;
    private Button mTryAgainButton, mCancelButton;
    private TextView stdUsername, stdDepartment, stdRegNo;
    private AppCompatImageView stdUserImg;
    boolean attendanceEnded = false;


    private Timer mTimerTimeout = null;
    private TimerTask mTaskTimeout = null;
    private Handler mHandlerTimeout;

    String date = "";
    String time = "";
    private NetworkCheck mNetworkCheck;
    private AttendanceActivityPresenter mPresenter;
    private FingerprintConverter mFingerprintConverter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        username = findViewById(R.id.username);
        courseCode = findViewById(R.id.courseCode);
        courseTitle = findViewById(R.id.courseTitle);
        dateTime = findViewById(R.id.dateTime);
        timeTxt = findViewById(R.id.timeTxt);
        searchBtPanel = findViewById(R.id.searchBtPanel);
        fingerPanel = findViewById(R.id.fingerPanel);
        searchBT = findViewById(R.id.searchBT);
        passedStudent = findViewById(R.id.passedStudent);
        startAttendancePanel = findViewById(R.id.startAttendancePanel);
        btnStartAttendance = findViewById(R.id.btnStartAttendance);
        finishAttendanceBtn = findViewById(R.id.finishAttendanceBtn);
        mAuthorizeDialog = new Dialog(this);
        mErrorMsg = mAuthorizeDialog.findViewById(R.id.errorMsg);

//        when clicked attendance is concluded
        finishAttendanceBtn.setOnClickListener(view -> {
            LecturerAuthorization();
            CaptureDelay();
//            AttendanceDone();
        });

//        Views to display when student identifies successfully
        stdUsername = findViewById(R.id.stdUsername);
        stdDepartment = findViewById(R.id.stdDepartment);
        stdRegNo = findViewById(R.id.stdRegNo);
        stdUserImg = findViewById(R.id.stdUserImg);

        btnStartAttendance.setOnClickListener(view -> {
            searchBtPanel.setVisibility(View.GONE);
            fingerPanel.setVisibility(View.VISIBLE);
            passedStudent.setVisibility(View.GONE);
            startAttendancePanel.setVisibility(View.GONE);
            mIsWork = false;
//            sdkUniversalEndPoints.TimeOutStop();
            SendCommand(CMD_CAPTUREHOST, null, 0);
        });

        searchBT.setOnClickListener(view -> {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(AttendanceActivity.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        });

        mPresenter = new AttendanceActivityPresenter(this, new AttendanceRepo());
        mNetworkCheck = new NetworkCheck(this);
        mStorageHelper = new StorageHelper(this);

        Date today = Calendar.getInstance().getTime();//getting date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formatter1 = new SimpleDateFormat("hh:mm:ss");
        date = formatter.format(today);
        time  = formatter1.format(today);
        dateTime.setText(date);
        timeTxt.setText(time);


        if (!getIntent().getExtras().isEmpty()){
            username.setText(getIntent().getExtras().getString("LecturerName", "Lecturer Name"));
            courseCode.setText(getIntent().getExtras().getString("CourseCode", "Course Code"));
            courseTitle.setText(getIntent().getExtras().getString("CourseTitle", "Course Title"));
            LecturerID = getIntent().getExtras().getString("Lecturer", "Lecturer ID");
            LecturerFingerprint = getIntent().getExtras().getString("lecturerFingerprint", "Lecturer Fingerprint");
            departmentalCourse = getIntent().getExtras().getString("departmentalCourse", "Departmental Course");
        }

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

        mPreferences = getApplicationContext().getSharedPreferences(USER_PREF,MODE_PRIVATE);
        studentID = mPreferences.getString("id", "Student ID");
        token = mPreferences.getString("token", "token");
        mFingerprintConverter = new FingerprintConverter(new Gson());
        GetStudents();

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
                            searchBtPanel.setVisibility(View.GONE);
                            fingerPanel.setVisibility(View.GONE);
                            startAttendancePanel.setVisibility(View.VISIBLE);
                            break;
                        case BluetoothReaderService.STATE_CONNECTING:
                            Toast.makeText(AttendanceActivity.this, "Trying to connect bluetooth...", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothReaderService.STATE_LISTEN:
                        case BluetoothReaderService.STATE_NONE:
                            searchBtPanel.setVisibility(View.VISIBLE);
                            fingerPanel.setVisibility(View.GONE);
                            startAttendancePanel.setVisibility(View.GONE);
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
        Log.d(TAG, "setupChat()");

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

            case CMD_CAPTUREHOST:
                Toast.makeText(this, "Capture Template ...", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    /**
     * Received the response from the device
     * @param databuf the data package response from the device
     * @param datasize the size of the data package
     */
    private void ReceiveCommand(byte[] databuf, int datasize) {
        if (mDeviceCmd == CMD_GETIMAGE) { //receiving the image data from the device
            Toast.makeText(this, "This is for image convertion", Toast.LENGTH_SHORT).show();
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
                        case CMD_CAPTUREHOST: {
                            int size = (byte) (mCmdData[5]) + ((mCmdData[6] << 8) & 0xFF00) - 1;
                            if (mCmdData[7] == 1 && !mAuthorizeDialog.isShowing()) {
                                memcpy(mMatData, 0, mCmdData, 8, size);
                                mMatSize = size;

                                String name = null;
                                String department = null;
                                String regNo = null;
                                String image = null;

                                boolean matchFlag = false;
                                for (AttendanceStudentObj attendanceStudentObj : mAttendanceStudentObjs){

                                    byte[] enroll;
                                    int ret = 0;
                                    if (attendanceStudentObj.getFingerprint() != null){
                                        enroll  = mFingerprintConverter.JsonToByteArray(attendanceStudentObj.getFingerprint());
                                        ret = FPMatch.getInstance().MatchFingerData(enroll,
                                                mMatData);
                                    }
                                    if (ret > 70) {

                                        for (Coursemate coursemate : coursemates){
                                            if (coursemate.getId().contains(attendanceStudentObj.getStudent())){
                                                name = coursemate.getName();
                                                department  = coursemate.getDepartment().getName();
                                                regNo = coursemate.getReg_no();
                                                image = coursemate.getImage();
                                                attendanceStudentObj.setPresent(true);
                                                matchFlag = true;
                                                break;
                                            }
                                        }

                                        stdUsername.setText(name);
                                        stdDepartment.setText(department);
                                        stdRegNo.setText(regNo);
                                        passedStudent.setVisibility(View.VISIBLE);
                                    }
                                    CaptureDelay();
                                }
                                if(!matchFlag){
                                    passedStudent.setVisibility(View.GONE);
                                    Toast toast = Toast.makeText(this, "Fingerprint did not match!",
                                            Toast.LENGTH_SHORT);
                                    TextView v  = toast.getView().findViewById(android.R.id.message);
                                    v.setTextColor(Color.RED);
                                    v.setTextSize(18f);
                                    toast.show();
                                    CaptureDelay();
                                }


                            } else if (mCmdData[7] == 1 && mAuthorizeDialog.isShowing()){
                                memcpy(mMatData, 0, mCmdData, 8, size);
                                mMatSize = size;
                                byte[] enroll;
                                int ret = 0;
                                if (LecturerFingerprint != null){
                                    enroll  = mFingerprintConverter.JsonToByteArray(LecturerFingerprint);
                                    ret = FPMatch.getInstance().MatchFingerData(enroll,
                                            mMatData);
                                }
                                if (ret > 70) {
                                    mAuthorizeDialog.dismiss();
                                    Toast.makeText(this, "Attendance ended successfully!", Toast.LENGTH_SHORT).show();
                                    AttendanceDone();
                                } else {
                                    mErrorMsg.setVisibility(View.VISIBLE);
                                    ShowErrorPanelControls();
                                }

                            }  else{
                                Toast toast = Toast.makeText(this, "Timeout, Please place your finger on the scanner", Toast.LENGTH_SHORT);
                                TextView v  = toast.getView().findViewById(android.R.id.message);
                                v.setTextColor(Color.RED);
                                v.setTextSize(18f);
                                toast.show();
                                CaptureDelay();
                            }
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

//    This method build an object of student list registered for the selected course
    void GetStudents(){
        coursemates = new Gson().fromJson(mStorageHelper.readJsonFile(studentID,
                "CourseMates.json"), new TypeToken<ArrayList<Coursemate>>(){}.getType());
        for (Coursemate coursemate: coursemates){
            for (RegisteredCourse registeredCourse : coursemate.getRegisteredCourses()){
                if (registeredCourse.getId().contains(departmentalCourse)){
                    mAttendanceStudentObjs.add(new AttendanceStudentObj(coursemate.getId(),false, coursemate.getFingerprint()));
                }
            }

        }
    }

    void CaptureDelay(){
        new Handler().postDelayed(() -> SendCommand(CMD_CAPTUREHOST, null, 0),1000);
    }


// building the final attendance object after attendance has been ended
    void AttendanceDone(){
        ArrayList<AttendanceStudentObj> students =  new ArrayList<>();
        for (AttendanceStudentObj obj : mAttendanceStudentObjs) {
            students.add(new AttendanceStudentObj(obj.getStudent(),obj.isPresent(),""));
        }
        AttendanceObj attendanceObj = new AttendanceObj(date,LecturerID,departmentalCourse,
                studentID,students);
        
        String jsonString = new Gson().toJson(attendanceObj);
        mStorageHelper.saveJsonFile("attendance", jsonString, studentID);
        String filepath = mStorageHelper.getFilePath("attendance.json",studentID);


        if (mNetworkCheck.isNetworkAvailable()){
//            mPresenter.UploadAttendance(token, filepath);
        }

    }

    private void ShowErrorPanelControls(){

        mTryAgainButton.setVisibility(View.VISIBLE);
        mTryAgainButton.setOnClickListener(view ->{
            mErrorMsg.setVisibility(View.GONE);
            SendCommand(CMD_CAPTUREHOST, null, 0);
        } );
        mCancelButton.setVisibility(View.VISIBLE);
        mCancelButton.setOnClickListener(view -> {
            attendanceEnded = false;
            mAuthorizeDialog.dismiss();
        });
    }


    private void LecturerAuthorization() {
        mAuthorizeDialog.setContentView(R.layout.authorize_end_attendance);
        mAuthorizeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAuthorizeDialog.setCanceledOnTouchOutside(false);
        mErrorMsg = mAuthorizeDialog.findViewById(R.id.errorMsg);
        mTryAgainButton = mAuthorizeDialog.findViewById(R.id.tryAgainBtn);
        mCancelButton = mAuthorizeDialog.findViewById(R.id.cancelBtn);
        mAuthorizeDialog.show();
    }

    @Override
    public void OnAttendanceUploaded(String message) {
        finish();
    }

    @Override
    public void OnUploadAttendanceFailed(String message) {

    }

    @Override
    public void OnUploadAttendanceError(Throwable e) {

    }
}
