package com.pixelus.dashclock.ext.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

import static android.bluetooth.BluetoothAdapter.STATE_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_ON;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_ON;
import static android.bluetooth.BluetoothClass.Device.Major.AUDIO_VIDEO;
import static android.bluetooth.BluetoothClass.Device.Major.COMPUTER;
import static android.bluetooth.BluetoothClass.Device.Major.HEALTH;
import static android.bluetooth.BluetoothClass.Device.Major.IMAGING;
import static android.bluetooth.BluetoothClass.Device.Major.MISC;
import static android.bluetooth.BluetoothClass.Device.Major.NETWORKING;
import static android.bluetooth.BluetoothClass.Device.Major.PERIPHERAL;
import static android.bluetooth.BluetoothClass.Device.Major.PHONE;
import static android.bluetooth.BluetoothClass.Device.Major.TOY;
import static android.bluetooth.BluetoothClass.Device.Major.UNCATEGORIZED;
import static android.bluetooth.BluetoothClass.Device.Major.WEARABLE;

/*
 * @author David Mouser
 */
public class BluetoothMessageBuilder {

  private static final String TAG = BluetoothMessageBuilder.class.getName();
  private BluetoothAdapter bluetoothAdaptor;
  private Context context;

  public BluetoothMessageBuilder withContext(final Context context) {
    this.context = context;
    return this;
  }

  public BluetoothMessageBuilder withBluetoothAdaptor(final BluetoothAdapter bluetoothAdaptor) {
    this.bluetoothAdaptor = bluetoothAdaptor;
    return this;
  }

  public String buildStatusMessage() {
    return getBluetoothStatus();
  }

  public String buildExpandedTitleMessage() {

    if (bluetoothAdaptor == null) {
      return context.getString(R.string.extension_expanded_title, R.string.bluetooth_status_unknown,
          getBluetoothStatus());
    }

    return context.getString(R.string.extension_expanded_title, getBluetoothStatus(), bluetoothAdaptor.getName());
  }

  public String buildExpandedBodyMessage() {

    if (bluetoothAdaptor == null) {
      return "";
    }

    final boolean bluetoothEnabled = bluetoothAdaptor.isEnabled();
    if (!bluetoothEnabled) {
      return "";
    }

    String pairedDevices = "";
    if (bluetoothEnabled) {

      Set<BluetoothDevice> bondedDevices = bluetoothAdaptor.getBondedDevices();
      if (bondedDevices.isEmpty()) {
        pairedDevices += context.getString(R.string.bluetooth_no_paired_devices);
      }

      Log.d(TAG, "Paired devices count: " + bondedDevices.size());
      Iterator<BluetoothDevice> bondedDevicesIterator = bondedDevices.iterator();
      while (bondedDevicesIterator.hasNext()) {

        BluetoothDevice bondedDevice = bondedDevicesIterator.next();
        pairedDevices += bondedDevice.getName()
            + " [" + getBluetoothMajorDeviceClass(bondedDevice.getBluetoothClass().getMajorDeviceClass()) + "]"
            + (bondedDevicesIterator.hasNext() ? "\n" : "");
      }
    }

    return context.getString(R.string.extension_expanded_body, pairedDevices);
  }

  private String getBluetoothStatus() {

    if (bluetoothAdaptor == null) {
      return context.getString(R.string.bluetooth_status_unknown);
    }

    int state = bluetoothAdaptor.getState();
    switch (state) {

      case STATE_OFF:
        return context.getString(R.string.bluetooth_status_disabled);
      case STATE_TURNING_ON:
        return context.getString(R.string.bluetooth_status_enabling);
      case STATE_ON:
        return context.getString(R.string.bluetooth_status_enabled);
      case STATE_TURNING_OFF:
        return context.getString(R.string.bluetooth_status_disabling);
      default:
        return context.getString(R.string.bluetooth_status_unknown);
    }
  }

  private String getBluetoothMajorDeviceClass(int majorDeviceClass) {

    switch (majorDeviceClass) {
      case AUDIO_VIDEO:
        return "audio/video";
      case COMPUTER:
        return "computer";
      case HEALTH:
        return "health";
      case IMAGING:
        return "imaging";
      case MISC:
        return "misc";
      case NETWORKING:
        return "networking";
      case PERIPHERAL:
        return "peripheral";
      case PHONE:
        return "phone";
      case TOY:
        return "toy";
      case UNCATEGORIZED:
        return "uncategorized";
      case WEARABLE:
        return "wearable";
      default:
        return "unknown!";
    }
  }
}
