package com.pixelus.dashclock.ext.mybluetooth.broadcast;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import com.pixelus.dashclock.ext.mybluetooth.BluetoothExtension;

import static com.google.android.apps.dashclock.api.DashClockExtension.UPDATE_REASON_SETTINGS_CHANGED;

public class BluetoothConnectionChangedBroadcastReceiver extends BroadcastReceiver {

  private static final String TAG = BluetoothConnectionChangedBroadcastReceiver.class.getName();

  private BluetoothExtension extension;

  public BluetoothConnectionChangedBroadcastReceiver(final BluetoothExtension extension) {

    this.extension = extension;
  }

  @Override
  public void onReceive(final Context context, final Intent intent) {

    Log.d(TAG, "Received broadcast for " + intent.getAction());

    try {
      final BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getExtras().get(BluetoothDevice.EXTRA_DEVICE);
      if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
        extension.addConnectedDevice(bluetoothDevice);
      } else if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
        extension.removeConnectedDevice(bluetoothDevice);
      }
      extension.onUpdateData(UPDATE_REASON_SETTINGS_CHANGED);
    } catch (NullPointerException e) {

      // Every so often an exception seems to be thrown by the DashClock api.
      // It seems that this exception is timing related.  Catch and log it for now!
      Crashlytics.log("NullPointerException caught when updating dashclock following receiving broadcast: "
          + intent.toString());
      Crashlytics.logException(e);
    }
  }
}