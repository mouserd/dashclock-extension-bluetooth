package com.pixelus.dashclock.ext.mybluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class ToggleBluetoothFragment extends DialogFragment {

  private static final String TAG = ToggleBluetoothFragment.class.getName();

  private boolean bluetoothEnabled;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {

    final Activity activity = getActivity();
    final Intent intent = getActivity().getIntent();
    final Bundle bundle = intent.getExtras();
    bluetoothEnabled = bundle.getBoolean(BluetoothExtension.BLUETOOTH_ENABLED);

    int message = R.string.toggle_bluetooth_to_enabled_message;
    if (bluetoothEnabled) {
      message = R.string.toggle_bluetooth_to_disabled_message;
    }

    return new AlertDialog.Builder(activity)
        .setMessage(message)
        .setPositiveButton(R.string.toggle_bluetooth_affirmative_button, new DialogClickListener(activity))
        .setNegativeButton(R.string.toggle_bluetooth_negative_button, new DialogClickListener(activity))
        .create();

  }

  private class DialogClickListener implements DialogInterface.OnClickListener {

    private Activity activity;

    private DialogClickListener(final Activity context) {
      this.activity = context;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int id) {

      Log.d(TAG, "Handling " + (BUTTON_POSITIVE == id ? "affirmative" : "negative") + " click event");

      if (BUTTON_POSITIVE == id) {

        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
          if (bluetoothEnabled) {

            Log.d(TAG, "Disabling Bluetooth...");
            bluetoothAdapter.disable();
          } else {

            Log.d(TAG, "Enabling Bluetooth...");
            bluetoothAdapter.enable();
          }
        }
      }

      activity.finish();
    }
  }
}