package com.amg.ips.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amg.ips.R;
import com.amg.ips.model.DeviceModel;

import java.util.List;
import java.util.UUID;

import static com.amg.ips.Utilities.DEVICE_NO_KEY;
import static com.amg.ips.ui.PlaceholderFragment.BLEDevices;
import static com.amg.ips.ui.PlaceholderFragment.ScannedDevices;

public class DeviceDetailsActivity extends AppCompatActivity {

    private BluetoothGatt bluetoothGatt;
    TextView addressTextView;
    TextView rssiTextView;
    TextView uuidTextView;
    TextView tx_powerTextView;
    TextView distanceTextView;
    private static DeviceModel device = null;
    private static int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);
        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra(DEVICE_NO_KEY, 0);
            device = ScannedDevices.get(position);
            if (device != null) {
                final String deviceName = device.getName();
                if (deviceName != null && deviceName.length() > 0) {
                    setTitle(deviceName);
                } else {
                    setTitle("Unknown Device");
                }
                addressTextView = (TextView) findViewById(R.id.address);
                addressTextView.setText(device.getAddress());
                rssiTextView = (TextView) findViewById(R.id.rssi);
                rssiTextView.setText(device.getRssi() + "");
                uuidTextView = (TextView) findViewById(R.id.uuid);
                tx_powerTextView = (TextView) findViewById(R.id.tx_power);
                tx_powerTextView.setText(device.getTx_power() + "");
                distanceTextView = (TextView) findViewById(R.id.distance);
                distanceTextView.setText(device.getR() + "");
            }
        } else {
            setTitle("Unknown Device");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bluetoothGatt == null && device != null) {
            connectToDeviceSelected(BLEDevices.get(position));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bluetoothGatt != null) {
            disconnectDeviceSelected();
        }
    }


    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            DeviceDetailsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(DeviceDetailsActivity.this, "device read or wrote to", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            System.out.println(newState);
            switch (newState) {
                case 0:
                    DeviceDetailsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
//                            Toast.makeText(DeviceDetailsActivity.this, "device disconnected", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 2:
                    DeviceDetailsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
//                            Toast.makeText(DeviceDetailsActivity.this, "device connected", Toast.LENGTH_SHORT).show();
                        }
                    });
                    bluetoothGatt.discoverServices();
                    break;
                default:
                    DeviceDetailsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
//                            Toast.makeText(DeviceDetailsActivity.this, "we encounterned an unknown state", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            DeviceDetailsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
//                    Toast.makeText(DeviceDetailsActivity.this, "device services have been discovered", Toast.LENGTH_SHORT).show();
                }
            });
            displayGattServices(bluetoothGatt.getServices());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                device.setUuid(characteristic.getUuid().toString());
//                uuidTextView.setText(device.getUuid().toString());
//            }
        }
    };

    public void connectToDeviceSelected(BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(this, false, btleGattCallback);
    }

    public void disconnectDeviceSelected() {
        bluetoothGatt.disconnect();
        bluetoothGatt = null;
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return;
        }

        for (BluetoothGattService gattService : gattServices) {
            final UUID uuid = gattService.getUuid();
            DeviceDetailsActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (device.getUuid() == null) {
                        device.setUuid(uuid.toString());
                        uuidTextView.setText(device.getUuid().toString());
                    }
                }
            });
        }
    }
}