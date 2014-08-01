package com.pixelus.dashclock.ext.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.crashlytics.android.Crashlytics;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;
import com.pixelus.dashclock.ext.mybluetooth.broadcast.BluetoothConnectionChangedBroadcastReceiver;
import com.pixelus.dashclock.ext.mybluetooth.broadcast.BluetoothStateChangedBroadcastReceiver;
import com.pixelus.dashclock.ext.mybluetooth.builder.BluetoothMessageBuilder;

import java.util.HashSet;
import java.util.Set;

import static android.bluetooth.BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED;
import static android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED;
import static android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED;
import static android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED;

public class BluetoothExtension extends DashClockExtension {

  public static final String TAG = BluetoothExtension.class.getName();

  public static final String EXTRA_BLUETOOTH_ENABLED = "com.pixelus.dashclock.ext.mybluetooth.BLUETOOTH_ENABLED";
  public static final String EXTRA_PAIRED_DEVICES = "com.pixelus.dashclock.ext.mybluetooth.PAIRED_DEVICES";
  public static final String ACTION_BLUETOOTH_CONNECTED_SERVICES =
      "com.pixelus.dashclock.ext.mybluetooth.BLUETOOTH_CONNECTED_SERVICES";

  public static final String PREF_SHOW_DEVICE_NAME = "show_device_name";
  public static final String PREF_SHOW_CONNECTED_DEVICES_ONLY = "show_connected_devices_only";
  public static final String PREF_PROMPT_TO_CONNECT_TO_DEVICE_ON_ENABLE = "prompt_to_connect_to_paired_device";

  private BluetoothConnectionChangedBroadcastReceiver bluetoothConnectionChangedBroadcastReceiver;
  private BluetoothStateChangedBroadcastReceiver bluetoothStateChangedBroadcastReceiver;
  private Set<BluetoothDevice> connectedDevices = new HashSet<BluetoothDevice>();

  @Override
  public void onCreate() {

    super.onCreate();
    Crashlytics.start(this);

    // On create, register to receive any changes to the bluetooth settings.  This ensures that we can
    // update our extension status based on us toggling the state or something externally.
    bluetoothStateChangedBroadcastReceiver = new BluetoothStateChangedBroadcastReceiver(this);
    registerReceiver(bluetoothStateChangedBroadcastReceiver, new IntentFilter(ACTION_STATE_CHANGED));
    registerReceiver(bluetoothStateChangedBroadcastReceiver, new IntentFilter(ACTION_CONNECTION_STATE_CHANGED));
    registerReceiver(bluetoothStateChangedBroadcastReceiver, new IntentFilter(ACTION_BLUETOOTH_CONNECTED_SERVICES));

    bluetoothConnectionChangedBroadcastReceiver = new BluetoothConnectionChangedBroadcastReceiver(this);
    registerReceiver(bluetoothConnectionChangedBroadcastReceiver, new IntentFilter(ACTION_ACL_CONNECTED));
    registerReceiver(bluetoothConnectionChangedBroadcastReceiver, new IntentFilter(ACTION_ACL_DISCONNECTED));
  }

  @Override
  public void onDestroy() {

    unregisterReceiver(bluetoothStateChangedBroadcastReceiver);
    unregisterReceiver(bluetoothConnectionChangedBroadcastReceiver);
  }

  @Override
  public void onUpdateData(final int i) {

    final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    final boolean showDeviceName = sp.getBoolean(PREF_SHOW_DEVICE_NAME, true);
    final boolean showConnectedDevicesOnly = sp.getBoolean(PREF_SHOW_CONNECTED_DEVICES_ONLY, false);

    final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    final BluetoothMessageBuilder builder = new BluetoothMessageBuilder()
        .withContext(this)
        .withBluetoothAdaptor(bluetoothAdapter)
        .withDeviceNameShown(showDeviceName)
        .withOnlyConnectedDevicesShown(showConnectedDevicesOnly)
        .withConnectedDevices(connectedDevices);

    publishUpdate(new ExtensionData()
        .visible(true)
        .icon(R.drawable.ic_launcher)
        .status(builder.buildStatusMessage())
        .expandedTitle(builder.buildExpandedTitleMessage())
        .expandedBody(builder.buildExpandedBodyMessage())
        .clickIntent(createClickIntent()));
  }

  private Intent createClickIntent() {

    final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (bluetoothAdapter != null) {
      final Intent toggleBluetoothIntent = new Intent(this, ToggleBluetoothDialogActivity.class);
      toggleBluetoothIntent.putExtra(EXTRA_BLUETOOTH_ENABLED, bluetoothAdapter.isEnabled());

      return toggleBluetoothIntent;
    }

    return null;
  }

  public void addConnectedDevice(BluetoothDevice bluetoothDevice) {
    connectedDevices.add(bluetoothDevice);
  }

  public void removeConnectedDevice(BluetoothDevice bluetoothDevice) {
    connectedDevices.remove(bluetoothDevice);
  }
}