package com.example.android.peripheraluribeacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BeaconSetupActivity extends Activity implements
        View.OnClickListener {
    private static final String TAG =
            BeaconSetupActivity.class.getSimpleName();

    /* UI to control advertise value */
    private EditText mUrlText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mUrlText = (EditText) findViewById(R.id.text_url);

        findViewById(R.id.button_start).setOnClickListener(this);
        findViewById(R.id.button_stop).setOnClickListener(this);

        final Uri uri = getIntent().getData();
        if (uri != null) {
            Log.d(TAG, "Received incoming Uri: " + uri.toString());
            mUrlText.setText(uri.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetoothStatus();
    }

    @Override
    public void onClick(View v) {
        Intent serviceIntent = new Intent(this, EddystoneUrlService.class);
        switch (v.getId()) {
            case R.id.button_start:
                Uri uri = Uri.parse(mUrlText.getText().toString());
                serviceIntent.setData(uri);
                startService(serviceIntent);
                break;
            case R.id.button_stop:
                stopService(serviceIntent);
                break;
            default:
                throw new IllegalArgumentException("Unknown view id");
        }
    }

    private boolean checkBluetoothStatus() {
        BluetoothManager manager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        /*
         * We need to enforce that Bluetooth is first enabled, and take the
         * user to settings to enable it if they have not done so.
         */
        if (adapter == null || !adapter.isEnabled()) {
            //Bluetooth is disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return false;
        }

        /*
         * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
         * from installing on these devices, but this will allow test devices or other
         * sideloads to report whether or not the feature exists.
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        /*
         * Check for advertising support. Not all devices are enabled to advertise
         * Bluetooth LE data.
         */
        if (!adapter.isMultipleAdvertisementSupported()) {
            Toast.makeText(this, "No Advertising Support.", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        return true;
    }
}
