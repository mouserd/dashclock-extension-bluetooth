package com.pixelus.dashclock.ext.mybluetooth.builder;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import com.pixelus.dashclock.ext.mybluetooth.BluetoothExtension;
import com.pixelus.dashclock.ext.mybluetooth.R;

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
  private BluetoothExtension context;
  private boolean showConnectedDevicesOnly;
  private Set<BluetoothDevice> connectedDevices;
  private boolean showDeviceName;

  public BluetoothMessageBuilder withContext(final BluetoothExtension context) {
    this.context = context;
    return this;
  }

  public BluetoothMessageBuilder withBluetoothAdaptor(final BluetoothAdapter bluetoothAdaptor) {
    this.bluetoothAdaptor = bluetoothAdaptor;
    return this;
  }

  public BluetoothMessageBuilder withDeviceNameShown(boolean showDeviceName) {
    this.showDeviceName = showDeviceName;
    return this;
  }

  public BluetoothMessageBuilder withOnlyConnectedDevicesShown(final boolean showConnectedDevicesOnly) {
    this.showConnectedDevicesOnly = showConnectedDevicesOnly;
    return this;
  }

  public BluetoothMessageBuilder withConnectedDevices(final Set<BluetoothDevice> connectedDevices) {
    this.connectedDevices = connectedDevices;
    return this;
  }

  public String buildStatusMessage() {
    return getBluetoothStatus();
  }

  public String buildExpandedTitleMessage() {

    return context.getString(R.string.extension_expanded_title, getBluetoothStatus() + " " + getDeviceName());
  }

  private String getDeviceName() {

    if (showDeviceName) {

      if (bluetoothAdaptor == null) {
        return context.getString(R.string.extension_expanded_title_device_name, R.string.bluetooth_status_unknown);
      }

      return context.getString(R.string.extension_expanded_title_device_name, bluetoothAdaptor.getName());
    }

    return "";
  }

  public String buildExpandedBodyMessage() {

    if (bluetoothAdaptor == null) {
      return "";
    }

    final boolean bluetoothEnabled = bluetoothAdaptor.isEnabled();
    if (!bluetoothEnabled) {
      return "";
    }

    String deviceList = "";

    final Set<BluetoothDevice> bluetoothDevices = getBluetoothDevices();
    if (bluetoothDevices.isEmpty()) {
      deviceList += context.getString(R.string.bluetooth_no_devices);
    }

    Log.d(TAG, "Paired devices count: " + bluetoothDevices.size());
    final Iterator<BluetoothDevice> bondedDevicesIterator = bluetoothDevices.iterator();
    while (bondedDevicesIterator.hasNext()) {

      BluetoothDevice bondedDevice = bondedDevicesIterator.next();
      deviceList += bondedDevice.getName()
          + " [" + getBluetoothMajorDeviceClass(bondedDevice.getBluetoothClass().getMajorDeviceClass()) + "]"
          + (bondedDevicesIterator.hasNext() ? "\n" : "");
    }

    return context.getString(
        showConnectedDevicesOnly ? R.string.extension_expanded_body_connected_devices :
            R.string.extension_expanded_body_paired_devices, deviceList);
  }

  private Set<BluetoothDevice> getBluetoothDevices() {

    if (showConnectedDevicesOnly) {

      return connectedDevices;
    }

    return bluetoothAdaptor.getBondedDevices();
  }


  private String getBluetoothStatus() {

    if (bluetoothAdaptor == null) {
      return context.getString(R.string.bluetooth_status_unknown);
    }

    switch (bluetoothAdaptor.getState()) {
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

  private String getBluetoothMajorDeviceClass(final int majorDeviceClass) {

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
