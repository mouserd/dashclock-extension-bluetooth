package com.pixelus.dashclock.ext.mybluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.pixelus.dashclock.ext.mybluetooth.BluetoothExtension.ACTION_BLUETOOTH_CONNECTED_SERVICES;

public class BluetoothProfileServiceListener implements BluetoothProfile.ServiceListener {

  private static final String TAG = BluetoothProfileServiceListener.class.getName();
  private Set<BluetoothDevice> connectedDevices = new HashSet<BluetoothDevice>();
  private Map<Integer, BluetoothProfile> discoveredProfiles = new HashMap<Integer, BluetoothProfile>();
  private Context context;

  public BluetoothProfileServiceListener(Context context) {
    this.context = context;
  }

  public Set<BluetoothDevice> getConnectedDevices() {
    return connectedDevices;
  }

  public Set<Integer> getDiscoveredProfiles() {
    return discoveredProfiles.keySet();
  }

  public void closeAllProxies(BluetoothAdapter bluetoothAdapter) {
    for (int profile : discoveredProfiles.keySet()) {
      bluetoothAdapter.closeProfileProxy(profile, discoveredProfiles.get(profile));
    }
  }

  @Override
  public void onServiceDisconnected(int profile) {
  }

  @Override
  public void onServiceConnected(int profile, BluetoothProfile proxy) {

    discoveredProfiles.put(profile, proxy);

    Log.d(TAG, "Bluetooth profile: " + profile);
    Log.d(TAG, "Checking connected services... (count: " + proxy.getConnectedDevices().size() + ")");
    for (BluetoothDevice device : proxy.getConnectedDevices()) {
      Log.d("onServiceConnected", "|" + device.getName() + " | " + device.getAddress() + " | " + proxy.getConnectionState
          (device) + "(connected = " + BluetoothAdapter.STATE_CONNECTED + ")");

      connectedDevices.add(device);
    }

    Log.d(TAG, "Sending broadcast service connected broadcast (profile: " + profile + ")");
    context.sendBroadcast(new Intent(ACTION_BLUETOOTH_CONNECTED_SERVICES));

//        bluetoothAdaptor.closeProfileProxy(profile, proxy);
  }
};