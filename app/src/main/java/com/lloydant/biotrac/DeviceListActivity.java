package com.lloydant.biotrac;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceListActivity extends AppCompatActivity {


    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private Dialog mBTDeviceDialog;
    private ListView mListView;
    private TextView mNoPairedDevice, mCaption;

    private Set<BluetoothDevice> pairedDevices;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBTDeviceDialog = new Dialog(this);
        mBTDeviceDialog.setContentView(R.layout.paired_devices_list);
        mListView = mBTDeviceDialog.findViewById(R.id.listView);
        mNoPairedDevice = mBTDeviceDialog.findViewById(R.id.noPairedDevice);
        mCaption = mBTDeviceDialog.findViewById(R.id.caption);
        mBTDeviceDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


//        Shows the list of paired bluetooth devices
        showList();

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

    }

    // The on-click listener for all devices in the ListViews
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
//            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the
            // View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            DeviceListActivity.this.setResult(Activity.RESULT_OK, intent);
            DeviceListActivity.this.finish();
        }
    };

    public void showList(){
        pairedDevices = bluetoothAdapter.getBondedDevices();


        ArrayList arrayList = new ArrayList();

        if (pairedDevices.size() > 0){
            for (BluetoothDevice bt : pairedDevices) arrayList.add(bt.getName() + " " + bt.getAddress());
            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,arrayList);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(mDeviceClickListener);
            mBTDeviceDialog.setCanceledOnTouchOutside(false);
        }else {
            mCaption.setVisibility(View.GONE);
            mNoPairedDevice.setVisibility(View.VISIBLE);
            mBTDeviceDialog.setOnCancelListener(dialogInterface -> DeviceListActivity.this.finish());
        }
        mBTDeviceDialog.show();
    }

}
