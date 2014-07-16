package com.pixelus.dashclock.ext.mybluetooth;

import android.content.Intent;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class MyBluetoothExtension extends DashClockExtension {

  public static final String TAG = MyBluetoothExtension.class.getName();
  public static final String BLUETOOTH_ENABLED = "com.pixelus.dashclock.ext.mybluetooth.BLUETOOTH_ENABLED";

  private boolean crashlyticsStarted = false;

  @Override
  protected void onUpdateData(int i) {

    if (!crashlyticsStarted) {
      Crashlytics.start(this);
      crashlyticsStarted = true;
    }

    String bluetoothStatus = "Disabled";
//    final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//    if (mBluetoothAdapter.isEnabled()) {
//      bluetoothStatus = "Enabled";
//      //mBluetoothAdapter.disable();
//    }

    Intent toggleBluetoothIntent = new Intent(this, ToggleBluetoothDialogActivity.class);
    toggleBluetoothIntent.putExtra(BLUETOOTH_ENABLED, true);

    // TODO: Create toggleBluetoothIntent and add 'extra' bluetooth status

    publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_launcher)
            .status(bluetoothStatus)
            .expandedTitle(bluetoothStatus)
            .clickIntent(toggleBluetoothIntent)
    );
  }


}