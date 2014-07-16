package com.pixelus.dashclock.ext.mybluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

// TODO Find alternatives to deprecated methods below - see DialogFragment
public class ToggleBluetoothDialogActivity extends Activity {

  private boolean bluetoothEnabled;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Intent intent = getIntent();
    final Bundle extras = intent.getExtras();

    bluetoothEnabled = extras.getBoolean(MyBluetoothExtension.BLUETOOTH_ENABLED);

    showDialog(0);
  }

  @Override
  public Dialog onCreateDialog(int id) {

    super.onCreateDialog(id);

    int message = R.string.toggle_bluetooth_to_enabled_message;
    if (bluetoothEnabled) {
      message = R.string.toggle_bluetooth_to_disabled_message;
    }

    // Use the Builder class for convenient dialog construction
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message)
        .setPositiveButton(R.string.toggle_bluetooth_affirmative_button, new DialogClickListener(this))
        .setNegativeButton(R.string.toggle_bluetooth_negative_button, new DialogClickListener(this));

    // Create the AlertDialog object and return it
    return builder.create();
  }

  private class DialogClickListener implements DialogInterface.OnClickListener {

    private Activity activity;

    private DialogClickListener(final Activity activity) {
      this.activity = activity;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int id) {

      if (AlertDialog.BUTTON_POSITIVE == id) {

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothEnabled) {
          mBluetoothAdapter.disable();
        } else {
          mBluetoothAdapter.enable();
        }
      }

      activity.finish();
    }
  }
}