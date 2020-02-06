package com.lloydant.biotrac.broadcasts;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Bluetooth {
    private IBluetooth mIBluetooth;

    public Bluetooth(IBluetooth IBluetooth) {
        mIBluetooth = IBluetooth;
    }

    public final  BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
         final String action = intent.getAction();

         if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
             final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

             switch (state){
                 case BluetoothAdapter.STATE_OFF:
                     Toast.makeText(context, "Bluetooth Switched Off", Toast.LENGTH_SHORT).show();
                     mIBluetooth.OnBluetoothTurnedOff();

                 case BluetoothAdapter.STATE_ON:
                     Toast.makeText(context, "Bluetooth Switched On", Toast.LENGTH_SHORT).show();
                     mIBluetooth.OnBluetoothTurnedOn();
             }
         }
        }
    };
}
