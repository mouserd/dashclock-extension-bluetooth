package com.pixelus.dashclock.ext.mybluetooth;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class ToggleBluetoothDialogActivity extends FragmentActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final DialogFragment dialog = new ToggleBluetoothFragment();
    dialog.show(getSupportFragmentManager(), "Toggle Bluetooth");
  }
}