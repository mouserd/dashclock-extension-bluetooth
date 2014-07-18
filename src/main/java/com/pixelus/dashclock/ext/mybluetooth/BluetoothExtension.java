package com.pixelus.dashclock.ext.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;

public class BluetoothExtension extends DashClockExtension {

  public static final String TAG = BluetoothExtension.class.getName();
  public static final String BLUETOOTH_ENABLED = "com.pixelus.dashclock.ext.mybluetooth.BLUETOOTH_ENABLED";

  private boolean crashlyticsStarted = false;
  private BluetoothToggledBroadcastReceiver bluetoothToggledBroadcastReceiver;

  @Override
  public void onCreate() {

    super.onCreate();

    Log.d(TAG, "Creating MyBluetoothExtension...");

    // On create, register to receive any changes to the bluetooth settings.  This ensures that we can
    // update our extension status based on us toggling the state or something externally.
    bluetoothToggledBroadcastReceiver = new BluetoothToggledBroadcastReceiver(this);
    registerReceiver(bluetoothToggledBroadcastReceiver, new IntentFilter(ACTION_STATE_CHANGED));
  }

  @Override public void onDestroy() {
    unregisterReceiver(bluetoothToggledBroadcastReceiver);
  }

  @Override
  protected void onUpdateData(int i) {

    if (!crashlyticsStarted) {
      Crashlytics.start(this);
      crashlyticsStarted = true;
    }

    final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    final BluetoothMessageBuilder builder = new BluetoothMessageBuilder()
        .withContext(this)
        .withBluetoothAdaptor(bluetoothAdapter);

    final ExtensionData extensionData = new ExtensionData()
        .visible(true)
        .icon(R.drawable.ic_launcher)
        .status(builder.buildStatusMessage())
        .expandedTitle(builder.buildExpandedTitleMessage())
        .expandedBody(builder.buildExpandedBodyMessage());

    if (bluetoothAdapter != null) {

      final Intent toggleBluetoothIntent = new Intent(this, ToggleBluetoothDialogActivity.class);
      toggleBluetoothIntent.putExtra(BLUETOOTH_ENABLED, bluetoothAdapter.isEnabled());

      extensionData.clickIntent(toggleBluetoothIntent);
    }

    publishUpdate(extensionData);
  }
}