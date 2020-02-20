package com.lloydant.biotrac;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fgtit.data.wsq;
import com.fgtit.reader.BluetoothReaderService;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

public class SDKUniversalEndPoints {

    private static final String TAG = "BluetoothReader";

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;
    BluetoothReaderService mChatService;


    public byte mUpImage[] = new byte[73728]; // image data
    private byte mCmdData[] = new byte[10240];
    public int mUpImageSize = 0;
    public int mUpImageCount = 0;



    private boolean mIsWork = false;
    private byte mDeviceCmd = 0x00;

    private Timer mTimerTimeout = null;
    private TimerTask mTaskTimeout = null;
    private Handler mHandlerTimeout;

    //directory for saving the fingerprint images
    private String sDirectory = "";

    private int mCmdSize = 0;

    public SDKUniversalEndPoints(BluetoothAdapter bluetoothAdapter, BluetoothReaderService chatService) {
        mBluetoothAdapter = bluetoothAdapter;
        mChatService = chatService;
    }

    public Intent ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            return discoverableIntent;
//            startActivity(discoverableIntent);
        }
        return null;
    }

    public String AddStatusList(String text) {
        return text;
//        mConversationArrayAdapter.add(text);
    }

    public String AddStatusListHex(byte[] data, int size) {
        String text = "";
        for (int i = 0; i < size; i++) {
            text = text + " " + Integer.toHexString(data[i] & 0xFF).toUpperCase() + "  ";
        }
        return text;
//        mConversationArrayAdapter.add(text);
    }

    /**
     * calculate the check sum of the byte[]
     * @param buffer byte[] required for calculating
     * @param size the size of the byte[]
     * @return the calculated check sum
     */
    public int calcCheckSum(byte[] buffer, int size) {
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum = sum + buffer[i];
        }
        return (sum & 0x00ff);
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



    //    converting recieved fingerprint to image and sizes
    public  Bitmap ConvertFingerprintToImage(int imgSize, int IMG200, int IMG288, int IMG360, byte[] databuf, int datasize ){
        if (imgSize == IMG200) {   //image size with 152*200
            memcpy(mUpImage, mUpImageSize, databuf, 0, datasize);
            mUpImageSize = mUpImageSize + datasize;
            if (mUpImageSize >= 15200) {
                File file = new File("/sdcard/test.raw");
                try {
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(mUpImage);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                byte[] bmpdata = getFingerprintImage(mUpImage, 152, 200, 0/*18*/);
//                textSize.setText("152 * 200");
                Bitmap image = BitmapFactory.decodeByteArray(bmpdata, 0, bmpdata.length);
                saveJPGimage(image);
                Log.d(TAG, "bmpdata.length:" + bmpdata.length);
//                fingerprintImage.setImageBitmap(image);
                mUpImageSize = 0;
                mUpImageCount = mUpImageCount + 1;
                mIsWork = false;
                return image;
//                    sdkUniversalEndPoints.AddStatusList("Display Image");
            }
        } else if (imgSize == IMG288) {   //image size with 256*288
            memcpy(mUpImage, mUpImageSize, databuf, 0, datasize);
            mUpImageSize = mUpImageSize + datasize;
            if (mUpImageSize >= 36864) {
                File file = new File("/sdcard/test.raw");
                try {
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(mUpImage);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                byte[] bmpdata = getFingerprintImage(mUpImage, 256, 288, 0/*18*/);
//                textSize.setText("256 * 288");
                Bitmap image = BitmapFactory.decodeByteArray(bmpdata, 0, bmpdata.length);
                saveJPGimage(image);

                byte[] inpdata = new byte[73728];
                int inpsize = 73728;
                System.arraycopy(bmpdata, 1078, inpdata, 0, inpsize);
                SaveWsqFile(inpdata, inpsize, "fingerprint.wsq");

                Log.d(TAG, "bmpdata.length:" + bmpdata.length);
//                fingerprintImage.setImageBitmap(image);
                mUpImageSize = 0;
                mUpImageCount = mUpImageCount + 1;
                mIsWork = false;
                return image;
//                    sdkUniversalEndPoints.AddStatusList("Display Image");
            }
        } else if (imgSize == IMG360) {   //image size with 256*360
            memcpy(mUpImage, mUpImageSize, databuf, 0, datasize);
            mUpImageSize = mUpImageSize + datasize;
            //AddStatusList("Image Len="+Integer.toString(mUpImageSize)+"--"+Integer.toString(mUpImageCount));
            if (mUpImageSize >= 46080) {
                File file = new File("/sdcard/test.raw");
                try {
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(mUpImage);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                byte[] bmpdata = getFingerprintImage(mUpImage, 256, 360, 0/*18*/);
//                textSize.setText("256 * 360");
                Bitmap image = BitmapFactory.decodeByteArray(bmpdata, 0, bmpdata.length);
                saveJPGimage(image);

                byte[] inpdata = new byte[92160];
                int inpsize = 92160;
                System.arraycopy(bmpdata, 1078, inpdata, 0, inpsize);
                SaveWsqFile(inpdata, inpsize, "fingerprint.wsq");

                Log.d(TAG, "bmpdata.length:" + bmpdata.length);
//                fingerprintImage.setImageBitmap(image);
                mUpImageSize = 0;
                mUpImageCount = mUpImageCount + 1;
                mIsWork = false;
                return image;
//                    sdkUniversalEndPoints.AddStatusList("Display Image");

            }

           /*     File f = new File("/sdcard/fingerprint.png");
                if (f.exists()) {
                    f.delete();
                }
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    image.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] inpdata=new byte[73728];
                int inpsize=73728;
                System.arraycopy(bmpdata,1078, inpdata, 0, inpsize);
                SaveWsqFile(inpdata,inpsize,"fingerprint.wsq");*/
        }
    return null;
    }


    /**
     * method of copying the byte[] data with specific length
     * @param dstbuf byte[] for storing the copied data with specific length
     * @param dstoffset the starting point for storing
     * @param srcbuf the source byte[] used for copying.
     * @param srcoffset the starting point for copying
     * @param size the length required to copy
     */
    public void memcpy(byte[] dstbuf, int dstoffset, byte[] srcbuf, int srcoffset, int size) {
        for (int i = 0; i < size; i++) {
            dstbuf[dstoffset + i] = srcbuf[srcoffset + i];
        }
    }



    /**
     * method for saving the fingerprint image as JPG
     * @param bitmap bitmap image
     */
    public void saveJPGimage(Bitmap bitmap) {
        String dir = sDirectory;
        String imageFileName = String.valueOf(System.currentTimeMillis());

        try {
            File file = new File(dir + imageFileName + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * method of saving the image into WSQ format
     * @param rawdata raw image data.
     * @param rawsize size of the raw image data.
     * @param filename the file name of the image.
     */
    public void SaveWsqFile(byte[] rawdata, int rawsize, String filename) {
        byte[] outdata = new byte[rawsize];
        int[] outsize = new int[1];

        if (rawsize == 73728) {
            wsq.getInstance().RawToWsq(rawdata, rawsize, 256, 288, outdata, outsize, 2.833755f);
        } else if (rawsize == 92160) {
            wsq.getInstance().RawToWsq(rawdata, rawsize, 256, 360, outdata, outsize, 2.833755f);
        }

        try {
            File fs = new File("/sdcard/" + filename);
            if (fs.exists()) {
                fs.delete();
            }
            new File("/sdcard/" + filename);
            RandomAccessFile randomFile = new RandomAccessFile("/sdcard/" + filename, "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.write(outdata, 0, outsize[0]);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create directory folder for storing the images
     */
    public void CreateDirectory() {
        sDirectory = Environment.getExternalStorageDirectory() + "/Fingerprint Images/";
        File destDir = new File(sDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

    }
}
