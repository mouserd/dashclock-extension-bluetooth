package com.pixelus.dashclock.ext.mybluetooth;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class ConnectPairedDeviceDialogActivity extends FragmentActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final DialogFragment dialog = new ConnectPairedDeviceFragment();
    dialog.show(getSupportFragmentManager(), "Connect Paired Device");
  }
}