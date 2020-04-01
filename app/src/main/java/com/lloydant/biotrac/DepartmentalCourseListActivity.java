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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.fpcore.FPMatch;
import com.fgtit.reader.BluetoothReaderService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lloydant.biotrac.customAdapters.DeptCourseAdapter;
import com.lloydant.biotrac.dagger2.BioTracApplication;
import com.lloydant.biotrac.helpers.FingerprintConverter;
import com.lloydant.biotrac.helpers.StorageHelper;
import com.lloydant.biotrac.models.Course;
import com.lloydant.biotrac.models.DepartmentalCourse;
import com.lloydant.biotrac.models.Lecturer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_DEVICE_NAME;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_READ;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_STATE_CHANGE;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_TOAST;
import static com.lloydant.biotrac.BluetoothReaderServiceVariables.MESSAGE_WRITE;


public class DepartmentalCourseListActivity extends AppCompatActivity implements DeptCourseAdapter.OnDepartmentalCourseListener {

    private ArrayList<Course> mCourseLists;
    private ArrayList<Lecturer> mLecturerArrayList;
    private Dialog mAuthorizeDialog;
    private TextView mErrorMsg;
    private Button mTryAgainButton, mCancelButton;
    private TextInputEditText searchBar;


    RecyclerView mRecyclerView;
    DeptCourseAdapter mDeptCourseAdapter;

    @Inject
    StorageHelper mStorageHelper;

    @Inject
    SharedPreferences mPreferences;


    // Debugging
    public static final String TAG = "BluetoothReader";

    private byte mCmdData[] = new byte[10240];

    private final static byte CMD_GETIMAGE = 0x30;      //GETIMAGE
    private final static byte CMD_CAPTUREHOST = 0x08;    //Caputre to Host



    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothReaderService mChatService;

    //definition of variables which used for storing the fingerprint template
    public byte mMatData[] = new byte[512];  // match FP template
    public int mMatSize = 0;

    private byte mDeviceCmd = 0x00;
    private int mCmdSize = 0;
    private boolean mIsWork = false;

    private Timer mTimerTimeout = null;
    private TimerTask mTaskTimeout = null;
    private Handler mHandlerTimeout;

    //dynamic setting of the permission for writing the data into phone memory
    private int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Inject
    FingerprintConverter mFingerprintConverter;

    @Inject
    Gson mGson;

    private View CoursesPanel, searchBtPanel;
    private Button searchBT;

    private String departmentalCourse;
    private  String studentID, CourseCode, CourseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departmental_course_list);
        mAuthorizeDialog = new Dialog(this);
        mCourseLists = new ArrayList<>();
        mStorageHelper = new StorageHelper(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.searchBar);
        CoursesPanel = findViewById(R.id.CoursesPanel);
        searchBtPanel = findViewById(R.id.searchBtPanel);
        searchBT = findViewById(R.id.searchBT);

        ((BioTracApplication) getApplication()).getAppComponent().inject(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mAuthorizeDialog.setCanceledOnTouchOutside(false);

        PopulateDeptCourseList();

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterRecycler(editable.toString());
            }
        });

        searchBT.setOnClickListener(view -> {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(DepartmentalCourseListActivity.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        });

        mAuthorizeDialog.setOnCancelListener(dialogInterface -> finish());


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
                            CoursesPanel.setVisibility(View.VISIBLE);
                            break;
                        case BluetoothReaderService.STATE_CONNECTING:
                            Toast.makeText(DepartmentalCourseListActivity.this, "Trying to connect bluetooth...", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothReaderService.STATE_LISTEN:
                        case BluetoothReaderService.STATE_NONE:
                            mAuthorizeDialog.dismiss();
                            searchBtPanel.setVisibility(View.VISIBLE);
                            CoursesPanel.setVisibility(View.GONE);
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

    private void AddStatusListHex(byte[] data, int size) {
        String text = "";
        for (int i = 0; i < size; i++) {
            text = text + " " + Integer.toHexString(data[i] & 0xFF).toUpperCase() + "  ";
        }
    }

    private void filterRecycler(String text) {
        //new array list that will hold the filtered data
        ArrayList<Course> courses = new ArrayList<>();

        //looping through existing elements
        for (Course course: mCourseLists) {
            //if the existing elements contains the search input
            if (course.getCode().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                courses.add(course);
            }
        }
        //calling a method of the adapter class and passing the filtered list
        mDeptCourseAdapter.filterList(courses);
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
            case CMD_CAPTUREHOST:
                Toast.makeText(this, "Capture Template ...", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void onDepartmentalCourseClick(View view, int position) {
        mLecturerArrayList = mCourseLists.get(position).getAssgined_lecturers();
        departmentalCourse = mCourseLists.get(position).getId();
        CourseCode = mCourseLists.get(position).getCode();
        CourseTitle = mCourseLists.get(position).getTitle();
        LecturerAuthorization();
    }

//    populate departmental courses from local storage
    void PopulateDeptCourseList(){
        studentID = mPreferences.getString("id", "Student ID");
        ArrayList<DepartmentalCourse> departmentalCourses = mGson.fromJson(mStorageHelper.readJsonFile(studentID, "RegisteredCourses.json"), new TypeToken<ArrayList<DepartmentalCourse>>(){}.getType());
        for (DepartmentalCourse departmentalCourse : departmentalCourses){
           mCourseLists.addAll(departmentalCourse.getCourses());
        }
        mDeptCourseAdapter = new DeptCourseAdapter(mCourseLists,this);

        mRecyclerView.setAdapter(mDeptCourseAdapter);
    }

    private void LecturerAuthorization() {
        mAuthorizeDialog.setContentView(R.layout.authorize_attendance);
        mAuthorizeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mErrorMsg = mAuthorizeDialog.findViewById(R.id.errorMsg);
        mTryAgainButton = mAuthorizeDialog.findViewById(R.id.tryAgainBtn);
        mCancelButton = mAuthorizeDialog.findViewById(R.id.cancelBtn);
        mAuthorizeDialog.show();
        SendCommand(CMD_CAPTUREHOST, null, 0);
    }

    private void ShowErrorPanelControls(){

        mTryAgainButton.setVisibility(View.VISIBLE);
        mTryAgainButton.setOnClickListener(view ->{
            mErrorMsg.setVisibility(View.GONE);
            SendCommand(CMD_CAPTUREHOST, null, 0);
        } );
        mCancelButton.setVisibility(View.VISIBLE);
        mCancelButton.setOnClickListener(view -> finish());
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
                            if (mCmdData[7] == 1) {
                                memcpy(mMatData, 0, mCmdData, 8, size);
                                mMatSize = size;

                                boolean matchFlag = false;
                                for (Lecturer lecturer: mLecturerArrayList){

                                    byte[] enroll;
                                    int ret = 0;
                                    if (lecturer.getFingerprint() != null){
                                       enroll  = mFingerprintConverter.JsonToByteArray(lecturer.getFingerprint());
                                       ret = FPMatch.getInstance().MatchFingerData(enroll,
                                                mMatData);
                                    }

                                    if (ret > 70) {
                                        mAuthorizeDialog.dismiss();
                                        Intent intent = new Intent(DepartmentalCourseListActivity.this,AttendanceActivity.class);
                                        intent.putExtra("Lecturer", lecturer.getId());
                                        intent.putExtra("LecturerName",lecturer.getName());
                                        intent.putExtra("CourseCode",CourseCode);
                                        intent.putExtra("CourseTitle",CourseTitle);
                                        intent.putExtra("departmentalCourse", departmentalCourse);
                                        intent.putExtra("lecturerFingerprint", lecturer.getFingerprint());
                                        startActivity(intent);
                                        finish();
                                        matchFlag = true;
                                        break;
                                    }
                                }
                                if(!matchFlag){
                                    mErrorMsg.setVisibility(View.VISIBLE);
                                    ShowErrorPanelControls();
                                    break;
                                }
                                if(mLecturerArrayList.size() == 0){
                                    Toast.makeText(this, "No Lecturer found!!!",
                                            Toast.LENGTH_LONG).show();
                                }

                            } else
                                mAuthorizeDialog.dismiss();
                            Toast.makeText(this, "Command not recognized", Toast.LENGTH_SHORT).show();
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
//                    Log.d(TAG, "BT not enabled");
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

}
