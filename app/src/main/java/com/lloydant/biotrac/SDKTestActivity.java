package com.lloydant.biotrac;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fgtit.User;
import com.fgtit.data.wsq;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.printer.DataUtils;
import com.fgtit.reader.BluetoothReaderService;
import com.fgtit.utils.DBHelper;
import com.google.gson.Gson;
import com.lloydant.biotrac.helpers.FingerprintConverter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SDKTestActivity extends AppCompatActivity {

    // Debugging
    //directory for saving the fingerprint images
    private String sDirectory = "";
    private static final String TAG = "BluetoothReader";

    //default image size
    public static final int IMG_WIDTH = 256;
    public static final int IMG_HEIGHT = 288;
    public static final int IMG_SIZE = IMG_WIDTH * IMG_HEIGHT;
    public static final int WSQBUFSIZE = 200000;

    //other image size
    public static final int IMG200 = 200;
    public static final int IMG288 = 288;
    public static final int IMG360 = 360;

    //definition of commands
    private final static byte CMD_PASSWORD = 0x01;    //Password
    private final static byte CMD_ENROLID = 0x02;        //Enroll in Device
    private final static byte CMD_VERIFY = 0x03;        //Verify in Device
    private final static byte CMD_IDENTIFY = 0x04;    //Identify in Device
    private final static byte CMD_DELETEID = 0x05;    //Delete in Device
    private final static byte CMD_CLEARID = 0x06;        //Clear in Device

    private final static byte CMD_ENROLHOST = 0x07;    //Enroll to Host
    private final static byte CMD_CAPTUREHOST = 0x08;    //Caputre to Host
    private final static byte CMD_MATCH = 0x09;        //Match
    private final static byte CMD_GETIMAGE = 0x30;      //GETIMAGE
    private final static byte CMD_GETCHAR = 0x31;       //GETDATA


    private final static byte CMD_WRITEFPCARD = 0x0A;    //Write Card Data
    private final static byte CMD_READFPCARD = 0x0B;    //Read Card Data
    private final static byte CMD_CARDSN = 0x0E;        //Read Card Sn
    private final static byte CMD_GETSN = 0x10;

    private final static byte CMD_FPCARDMATCH = 0x13;   //

    private final static byte CMD_WRITEDATACARD = 0x14;    //Write Card Data
    private final static byte CMD_READDATACARD = 0x15;     //Read Card Data

    private final static byte CMD_PRINTCMD = 0x20;        //Printer Print
    private final static byte CMD_GETBAT = 0x21;
    private final static byte CMD_UPCARDSN = 0x43;
    private final static byte CMD_GET_VERSION = 0x22;        //Version

    private byte mDeviceCmd = 0x00;
    private boolean mIsWork = false;
    private byte mCmdData[] = new byte[10240];
    private int mCmdSize = 0;

    private Timer mTimerTimeout = null;
    private TimerTask mTaskTimeout = null;
    private Handler mHandlerTimeout;

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
    private ListView mConversationView;
    private ImageView fingerprintImage;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothReaderService mChatService = null;

    //definition of variables which used for storing the fingerprint template
    public byte mRefData[] = new byte[512]; //enrolled FP template data
    public int mRefSize = 0;
    public byte mMatData[] = new byte[512];  // match FP template data
    public int mMatSize = 0;

    public byte mCardSn[] = new byte[7];
    public byte mCardData[] = new byte[4096];
    public int mCardSize = 0;

    public byte mBat[] = new byte[2];  // data of battery status
    public byte mUpImage[] = new byte[73728]; // image data
    public int mUpImageSize = 0;
    public int mUpImageCount = 0;


    public byte mRefCoord[] = new byte[512];
    public byte mMatCoord[] = new byte[512];

    public byte mIsoData[] = new byte[378];

    private int userId; // User ID number
    private SQLiteDatabase userDB; //SQLite database object
    private ArrayList<User> mUsers = new ArrayList<>();
    private FingerprintConverter mFingerprintConverter;

    private Button connectBT, enrollFP, captureFP;

    //dynamic setting of the permission for writing the data into phone memory
    private int REQUEST_PERMISSION_CODE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdktest);

        connectBT = findViewById(R.id.connectBT);
        enrollFP = findViewById(R.id.enrollFP);
        captureFP = findViewById(R.id.captureFP);


        //checking the permission
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,
                        REQUEST_PERMISSION_CODE);
            }
        }

        mFingerprintConverter = new FingerprintConverter(new Gson());

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

        //initialize the SQLite
        userId = 1;
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
                            enrollFP.setVisibility(View.VISIBLE);
                            captureFP.setVisibility(View.VISIBLE);

                            break;
                        case BluetoothReaderService.STATE_CONNECTING:
                            Toast.makeText(SDKTestActivity.this, "Bluetooth trying to connect...", Toast.LENGTH_SHORT).show();
                            break;
                        case BluetoothReaderService.STATE_LISTEN:
                        case BluetoothReaderService.STATE_NONE:
                            enrollFP.setVisibility(View.GONE);
                            captureFP.setVisibility(View.GONE);
                            Toast.makeText(SDKTestActivity.this, "Bluetooth disconnected", Toast.LENGTH_SHORT).show();
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
//        mConversationArrayAdapter.add(text);
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
    }

    /**
     * configure for the UI components
     */
    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
//        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
//        mConversationView = (ListView) findViewById(R.id.in);
//        mConversationView.setAdapter(mConversationArrayAdapter);

        fingerprintImage = (ImageView) findViewById(R.id.imageView1);

        connectBT.setOnClickListener(view -> {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(SDKTestActivity.this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        });

        enrollFP.setOnClickListener(view -> SendCommand(CMD_ENROLHOST, null, 0));

        captureFP.setOnClickListener(view -> SendCommand(CMD_CAPTUREHOST, null, 0));

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

            case CMD_ENROLHOST:
                Toast.makeText(this, "Enroll Template ...", Toast.LENGTH_SHORT).show();
                break;
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
                        case CMD_ENROLHOST: {
                            int size = (byte) (mCmdData[5]) + ((mCmdData[6] << 8) & 0xFF00) - 1;
                            if (mCmdData[7] == 1) {
                                memcpy(mRefData, 0, mCmdData, 8, size);
                                mRefSize = size;
                                String temp = mFingerprintConverter.ByteToJsonString(mRefData);
                                mUsers.add(new User(userId, temp));

                                Toast.makeText(this, "Enrol Succeed with finger: " + userId, Toast.LENGTH_SHORT).show();

                                //save into database
//                                ContentValues values = new ContentValues();
//                                values.put(DBHelper.TABLE_USER_ID, userId);
//                                values.put(DBHelper.TABLE_USER_ENROL1, mRefData);
//                                userDB.insert(DBHelper.TABLE_USER, null, values);
//                                AddStatusList("Enrol Succeed with finger: " + userId);
                                userId += 1;


                            } else
                                Toast.makeText(this, "Search Fail", Toast.LENGTH_SHORT).show();
//                                AddStatusList();
                        }
                        break;
                        case CMD_CAPTUREHOST: {
                            int size = (byte) (mCmdData[5]) + ((mCmdData[6] << 8) & 0xFF00) - 1;
                            if (mCmdData[7] == 1) {
                                memcpy(mMatData, 0, mCmdData, 8, size);
                                mMatSize = size;

                                boolean matchFlag = false;
                                for (User user : mUsers){

                                    byte[] enroll  = mFingerprintConverter.JsonToByteArray(user.getFingerprint());

                                    int ret = FPMatch.getInstance().MatchFingerData(enroll,
                                            mMatData);
                                    if (ret > 70) {
                                        Toast.makeText(this, "Match OK,Finger = " + user.getId() + "!!", Toast.LENGTH_SHORT).show();
//                                        AddStatusList("Match OK,Finger = " + user.getId() + "!!");
                                        matchFlag = true;
                                        break;
                                    }
                                }
//                                Cursor cursor = userDB.query(DBHelper.TABLE_USER, null, null,
//                                        null, null, null, null, null);
//                                boolean matchFlag = false;
//                                while (cursor.moveToNext()) {
//                                    int id = cursor.getInt(cursor.getColumnIndex(DBHelper
//                                            .TABLE_USER_ID));
//                                    byte[] enrol1 = cursor.getBlob(cursor.getColumnIndex(DBHelper
//                                            .TABLE_USER_ENROL1));
//                                    int ret = FPMatch.getInstance().MatchFingerData(enrol1,
//                                            mMatData);
//                                    if (ret > 70) {
//                                        AddStatusList("Match OK,Finger = " + id + "!!");
//                                        matchFlag = true;
//                                        break;
//                                    }
//                                }
                                if(!matchFlag){
                                    Toast.makeText(this, "Match Fail!!", Toast.LENGTH_SHORT).show();
//                                    AddStatusList("Match Fail !!");
                                }
                                if(mUsers.size() == 0){
                                    Toast.makeText(this, "User list empty!!", Toast.LENGTH_SHORT).show();
                                }

                            } else
                                Toast.makeText(this, "Search Fail!!", Toast.LENGTH_SHORT).show();
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

}
