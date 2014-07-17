package com.pixelus.dashclock.ext.mybluetooth;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class ToggleBluetoothDialogActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        DialogFragment dialog = new ToggleBluetoothFragment();
        dialog.show(getSupportFragmentManager(), "Toggle Bluetooth");
    }
}