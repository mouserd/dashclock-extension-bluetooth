package com.pixelus.dashclock.ext.mybluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.ListView;
import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.util.UUID;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.pixelus.dashclock.ext.mybluetooth.BluetoothExtension.EXTRA_PAIRED_DEVICES;

public class ConnectPairedDeviceFragment extends DialogFragment {

  private static final String TAG = ConnectPairedDeviceFragment.class.getName();

  private CharSequence[] pairedDevices;
  private String selectedDeviceName;

  @Override
  public Dialog onCreateDialog(final Bundle savedInstanceState) {

    final Activity activity = getActivity();
    final Intent intent = getActivity().getIntent();
    final Bundle bundle = intent.getExtras();
    pairedDevices = bundle.getCharSequenceArray(EXTRA_PAIRED_DEVICES);

    Log.d(TAG, "Paired devices: " + pairedDevices.length);

    return new AlertDialog.Builder(activity)
        .setTitle(R.string.connect_to_device_dialog_title)
        .setSingleChoiceItems(pairedDevices, 0, connectDialogClickListener)
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

      activity.finish();

      Log.d(TAG, "Handling " + (BUTTON_POSITIVE == id ? "affirmative" : "negative") + " click event");

      if (BUTTON_POSITIVE == id && selectedDeviceName != null) {

        connectToSelectedPairedDevice();
      }
    }
  }

  private void connectToSelectedPairedDevice() {

    final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {

      if (device.getName().equals(selectedDeviceName)) {

        try {
          final BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(device.getAddress());
          final BluetoothSocket remoteDeviceSocket = remoteDevice.createRfcommSocketToServiceRecord(UUID.randomUUID());
          remoteDeviceSocket.connect();

        } catch (IOException e) {
          Crashlytics.logException(e);
        }
        return;
      }
    }
  }


  private DialogInterface.OnClickListener connectDialogClickListener = new DialogInterface.OnClickListener() {

    @Override
    public void onClick(final DialogInterface dialogInterface, final int i) {

      final ListView listView = ((AlertDialog) dialogInterface).getListView();
      selectedDeviceName = (String) listView.getAdapter().getItem(listView.getCheckedItemPosition());

      Log.d(TAG, "Selected device: " + selectedDeviceName);
    }
  };
}

