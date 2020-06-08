package com.amg.ips.ui;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amg.ips.R;
import com.amg.ips.adapter.StoredDevicesAdapter;
import com.amg.ips.model.DeviceModel;

import java.util.ArrayList;

import static com.amg.ips.Utilities.*;
import static com.amg.ips.ui.PlaceholderFragment.*;

public class RoomDetailsActivity extends AppCompatActivity {

    private static String room = null;
    private RecyclerView devicesRecyclerView;
    private static StoredDevicesAdapter storedDevicesAdapter;
    ArrayList<DeviceModel> thisRoomStoredDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

//        context = this;
        Intent intent = getIntent();
        if (intent != null) {
            room = intent.getStringExtra(ROOM_NO_KEY);
            if (room != null) {
                setTitle(room);
                thisRoomStoredDevices = new ArrayList<>();
                if (StoredDevices.size() > 0) {
                    for (DeviceModel device : StoredDevices) {
                        if (device.getRoom().equals(room)) {
                            thisRoomStoredDevices.add(device);
                        }
                    }
                }
                devicesRecyclerView = (RecyclerView) findViewById(R.id.storedDevicesListView);
                devicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                storedDevicesAdapter = new StoredDevicesAdapter(this,thisRoomStoredDevices);
                devicesRecyclerView.setAdapter(storedDevicesAdapter);
                findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                RoomDetailsActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        View view = getLayoutInflater().inflate(R.layout.add_device_dialog, null);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RoomDetailsActivity.this);
                                        builder.setTitle("Add Device");
                                        final EditText macAddressEditText = (EditText) view.findViewById(R.id.address);
                                        final EditText posxEditText = (EditText) view.findViewById(R.id.posx);
                                        final EditText posyEditText = (EditText) view.findViewById(R.id.posy);
                                        final EditText tx_powerEditText = (EditText) view.findViewById(R.id.tx_power);

                                        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new AsyncTask<DeviceModel, Void, DeviceModel>() {

                                                    @Override
                                                    protected DeviceModel doInBackground(DeviceModel... params) {
                                                        ContentValues values = new ContentValues();
                                                        values.put(DEVICE_MACADDRESS, params[0].getAddress());
                                                        values.put(DEVICE_ROOM, params[0].getRoom());
                                                        values.put(DEVICE_POSX, params[0].getPosx());
                                                        values.put(DEVICE_POSY, params[0].getPosy());
                                                        values.put(DEVICE_TX_POWER, params[0].getTx_power());
                                                        RoomDetailsActivity.this.getContentResolver().insert(DEVICES_BASE_URI, values);
                                                        return params[0];
                                                    }

                                                    @Override
                                                    protected void onPostExecute(DeviceModel device) {
                                                        if (device != null) {
                                                            StoredDevices.add(device);
                                                            thisRoomStoredDevices.add(device);
                                                            storedDevicesAdapter.notifyDataChange(thisRoomStoredDevices);
                                                        }
                                                    }
                                                }.execute(new DeviceModel(macAddressEditText.getText().toString(), "", room, Float.valueOf(posxEditText.getText().toString())
                                                        , Float.valueOf(posyEditText.getText().toString()), Integer.valueOf(tx_powerEditText.getText().toString()), DEFAULT_RSSI));
                                            }
                                        });
                                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.setView(view);
                                        builder.show();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    }
}
