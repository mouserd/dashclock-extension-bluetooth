package com.pixelus.dashclock.ext.mybluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.pixelus.dashclock.ext.mybluetooth.BluetoothExtension.EXTRA_PAIRED_DEVICES;

public class ToggleBluetoothFragment extends DialogFragment {

  private static final String TAG = ToggleBluetoothFragment.class.getName();

  private boolean bluetoothEnabled;

  @Override
  public Dialog onCreateDialog(final Bundle savedInstanceState) {

    final Activity activity = getActivity();

    final Intent intent = getActivity().getIntent();
    final Bundle bundle = intent.getExtras();
    bluetoothEnabled = bundle.getBoolean(BluetoothExtension.EXTRA_BLUETOOTH_ENABLED);

    int title = R.string.toggle_bluetooth_to_enabled_title;
    if (bluetoothEnabled) {
      title = R.string.toggle_bluetooth_to_disabled_title;
    }

    return new AlertDialog.Builder(activity)
        .setTitle(title)
        .setPositiveButton(R.string.toggle_bluetooth_affirmative_button, new DialogClickListener(activity))
        .setNegativeButton(R.string.toggle_bluetooth_negative_button, new DialogClickListener(activity))
        .create();
  }

  private class DialogClickListener implements DialogInterface.OnClickListener {

    private Activity activity;

    private DialogClickListener(final Activity activity) {
      this.activity = activity;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int id) {

      Log.d(TAG, "Handling " + (BUTTON_POSITIVE == id ? "affirmative" : "negative") + " click event");

      getActivity().finish();
      if (BUTTON_POSITIVE == id) {

        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
          return;
        }
        if (bluetoothEnabled) {

          Log.d(TAG, "Disabling Bluetooth...");
          bluetoothAdapter.disable();
        } else {

          Log.d(TAG, "Enabling Bluetooth...");
          bluetoothAdapter.enable();

          // Based on a user preference... show the dialog to connect to a paired device
          final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
          final boolean promptToConnectToDeviceOnEnable =
              sp.getBoolean(BluetoothExtension.PREF_PROMPT_TO_CONNECT_TO_DEVICE_ON_ENABLE, false);

          if (promptToConnectToDeviceOnEnable) {
            displayConnectToPairedDeviceDialog(bluetoothAdapter);
          }
        }
      }
    }

    private void displayConnectToPairedDeviceDialog(final BluetoothAdapter bluetoothAdapter) {

      // Ensure that the enabling of bluetooth has been completed.
      while (!bluetoothAdapter.isEnabled()) {
      }

      final Set<BluetoothDevice> pairedDevicesSet = bluetoothAdapter.getBondedDevices();
      if (pairedDevicesSet.size() > 0) {

        final List<CharSequence> pairedDeviceNames = new ArrayList<CharSequence>();
        for (BluetoothDevice pairedDevice : pairedDevicesSet) {
          pairedDeviceNames.add(pairedDevice.getName());
        }

        final Intent connectIntent = new Intent(activity, ConnectPairedDeviceDialogActivity.class);
        connectIntent.putExtra(EXTRA_PAIRED_DEVICES, pairedDeviceNames.toArray(new CharSequence[pairedDeviceNames.size()]));
        startActivityForResult(connectIntent, 1);
      }
    }
  }
}