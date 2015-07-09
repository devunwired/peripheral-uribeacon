package com.example.android.peripheraluribeacon;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import org.uribeacon.beacon.UriBeacon;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class UriBeaconService extends Service {
    private static final String TAG = UriBeaconService.class.getSimpleName();

    private static final int NOTIFICATION_ID = 42;

    /* Full Bluetooth UUID that defines the URI Service */
    public static final ParcelUuid URI_SERVICE =
            ParcelUuid.fromString("0000fed8-0000-1000-8000-00805f9b34fb");

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothManager manager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = manager.getAdapter();
        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri uri = intent.getData();
        if (uri == null) {
            Log.w(TAG, "Intent received with null Uri");
            stopSelf();
        } else {
            restartAdvertising(uri);
            startForeground(NOTIFICATION_ID, buildForegroundNotification(uri));
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);

        stopAdvertising();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startAdvertising(Uri uri) {
        if (mBluetoothLeAdvertiser == null) return;

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(false)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

        try {
            AdvertiseData data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(false)
                    .setIncludeTxPowerLevel(false)
                    .addServiceUuid(URI_SERVICE)
                    .addServiceData(URI_SERVICE, buildDataPacket(uri))
                    .build();

            mBluetoothLeAdvertiser
                    .startAdvertising(settings, data, mAdvertiseCallback);
        } catch (URISyntaxException e) {
            Log.w(TAG, "Unable to start advertising", e);
        }
    }

    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser == null) return;

        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    private void restartAdvertising(Uri uri) {
        stopAdvertising();
        startAdvertising(uri);
    }

    private Notification buildForegroundNotification(Uri uri) {
        Intent notificationIntent = new Intent(this, BeaconSetupActivity.class);
        notificationIntent.setData(uri);
        PendingIntent trigger = PendingIntent
                .getActivity(this, 0, notificationIntent, 0);

        return new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Broadcasting URL")
                .setContentText("Broadcasting " + uri.toString())
                .setContentIntent(trigger)
                .build();
    }

    private byte[] buildDataPacket(Uri uri) throws URISyntaxException {
        //We cannot read the TX power from Android. This uses a sane default.
        UriBeacon model = new UriBeacon.Builder()
                .uriString(uri.toString())
                .build();
        byte[] uriBytes = model.getUriBytes();
        //Flags + Power + URI
        ByteBuffer buffer = ByteBuffer.allocateDirect(1 + 1 + uriBytes.length);
        buffer.put(model.getFlags());
        buffer.put(model.getTxPowerLevel()); // This is cheating…we don't really know it
        buffer.put(uriBytes);

        return byteBufferToArray(buffer);
    }

    private static byte[] byteBufferToArray(ByteBuffer bb) {
        byte[] bytes = new byte[bb.position()];
        bb.rewind();
        bb.get(bytes, 0, bytes.length);
        return bytes;
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: "+errorCode);
        }
    };
}
