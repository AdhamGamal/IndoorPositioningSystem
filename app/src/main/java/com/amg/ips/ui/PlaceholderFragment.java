package com.amg.ips.ui;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amg.ips.R;
import com.amg.ips.adapter.DevicesAdapter;
import com.amg.ips.adapter.RoomsAdapter;
import com.amg.ips.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.amg.ips.Utilities.*;
import static com.amg.ips.ui.MainActivity.btScanner;

public class PlaceholderFragment extends Fragment {
    //Scanned Devices Fragment
    private RecyclerView devicesRecyclerView;
    private static DevicesAdapter devicesAdapter;

    //Rooms Fragment
    private RecyclerView roomsRecyclerView;
    private static RoomsAdapter roomsAdapter;

    //Pposition Fragment
    private static TextView POSX_TextView;
    private static TextView POSY_TextView;
    private static TextView ROOM_TextView;

    //Booleans
    public static boolean IntializeVariables = true;
    private static int pos = 1;


    private static ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice bleDevice = result.getDevice();
//            Log.e(">>>>>>>>>>", bleDevice.getAddress());
            CheckDeviceNewbility(bleDevice, result);
            CheckDeviceSimilarity(bleDevice, result);
            Object[] data = CalPosition();
            if (data != null) {
//                if (data[0] != null && data[1] != null && data[2] != null) {
                POSX_TextView.setText(data[0] + "");
                POSY_TextView.setText(data[1] + "");
                ROOM_TextView.setText(data[2] + "");
//                }
            }
            devicesAdapter.notifyDataChange(ScannedDevices);
        }
    };


    public static void startScanning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public static void stopScanning() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }

    public PlaceholderFragment() {
    }

    public static PlaceholderFragment GetInstance(ArrayList<PlaceholderFragment> fragments, int index) {
        PlaceholderFragment fragment = fragments.get(index);
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView;
//        context = getActivity();
        pos = getArguments().getInt(INDEX);

        if (IntializeVariables) {
            ScannedMacAddresses = new ArrayList<>();
            StoredMacAddresses = new ArrayList<>();
            RoomDevicesAddresses = new ArrayList<>();
            Rooms = new ArrayList<>();
            ScannedDevices = new ArrayList<>();
            StoredDevices = new ArrayList<>();
            RoomDevices = new ArrayList<>();
            BLEDevices = new ArrayList<>();
            determineRoom = new HashMap<>();
            IntializeVariables = false;
        }

        if (pos == 0) {
            rootView = inflater.inflate(R.layout.position_fragment, container, false);
            POSX_TextView = (TextView) rootView.findViewById(R.id.POSX);
            POSY_TextView = (TextView) rootView.findViewById(R.id.POSY);
            ROOM_TextView = (TextView) rootView.findViewById(R.id.room);

        } else if (pos == 1) {
            rootView = inflater.inflate(R.layout.scan_fragment, container, false);
            devicesRecyclerView = (RecyclerView) rootView.findViewById(R.id.devicesListView);
            devicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            devicesAdapter = new DevicesAdapter(ScannedDevices);
            devicesRecyclerView.setAdapter(devicesAdapter);

        } else {
            rootView = inflater.inflate(R.layout.rooms_fragment, container, false);
            loadSavedFiles(rootView);

            rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    View view = getLayoutInflater().inflate(R.layout.add_room_dialog, null);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Add Room");
                                    final EditText RoomNameEditText = (EditText) view.findViewById(R.id.name);
                                    final EditText RoomXEditText = (EditText) view.findViewById(R.id.xroom);
                                    final EditText RoomYEditText = (EditText) view.findViewById(R.id.yroom);

                                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new AsyncTask<RoomModel, Void, RoomModel>() {

                                                @Override
                                                protected RoomModel doInBackground(RoomModel... params) {
                                                    if (params[0].getName().trim().length() == 0) {
                                                        return null;
                                                    } else {
                                                        ContentValues values = new ContentValues();
                                                        values.put(ROOM_NAME, params[0].getName());
                                                        values.put(ROOM_X, params[0].getPosx());
                                                        values.put(ROOM_Y, params[0].getPosy());

                                                        getContext().getContentResolver().insert(ROOMS_BASE_URI, values);
                                                        return params[0];
                                                    }
                                                }

                                                @Override
                                                protected void onPostExecute(RoomModel room) {
                                                    if (room != null) {
                                                        Rooms.add(room);
                                                        roomsAdapter.notifyDataChange(Rooms);
                                                    }
                                                }
                                            }.execute(new RoomModel(RoomNameEditText.getText().toString(), Float.valueOf(RoomXEditText.getText().toString()), Float.valueOf(RoomYEditText.getText().toString())));
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

        return rootView;
    }

    public void loadSavedFiles(final View view) {

        new AsyncTask<Void, Void, ArrayList<RoomModel>>() {

            @Override
            protected ArrayList<RoomModel> doInBackground(Void... params) {
                Rooms = new ArrayList<>();
                Cursor cursor = getContext().getContentResolver().query(ROOMS_BASE_URI, ROOM_COLUMNS, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        Rooms.add(new RoomModel(cursor.getString(0).trim(), cursor.getFloat(1), cursor.getFloat(2)));
                        RoomDevicesAddresses.add(cursor.getString(0).trim());
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                return Rooms;
            }

            @Override
            protected void onPostExecute(ArrayList<RoomModel> rooms) {
                roomsRecyclerView = (RecyclerView) view.findViewById(R.id.roomsListView);
                roomsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                roomsAdapter = new RoomsAdapter(getActivity(), rooms);
                roomsRecyclerView.setAdapter(roomsAdapter);
            }
        }.execute();

        new AsyncTask<Void, Void, ArrayList<DeviceModel>>() {

            @Override
            protected ArrayList<DeviceModel> doInBackground(Void... params) {
                StoredDevices = new ArrayList<>();
                StoredMacAddresses = new ArrayList<>();
                Cursor cursor = getContext().getContentResolver().query(DEVICES_BASE_URI, DEVICE_COLUMNS, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        StoredMacAddresses.add(cursor.getString(0).trim());
                        StoredDevices.add(new DeviceModel(cursor.getString(0).trim(), "", cursor.getString(1), cursor.getFloat(2), cursor.getFloat(3), cursor.getInt(4), DEFAULT_RSSI));
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                return StoredDevices;
            }
        }.execute();
    }


    //Arrays
    public static ArrayList<String> ScannedMacAddresses;
    public static ArrayList<String> StoredMacAddresses;
    public static ArrayList<String> RoomDevicesAddresses;
    public static ArrayList<RoomModel> Rooms;
    public static ArrayList<DeviceModel> ScannedDevices;
    public static ArrayList<DeviceModel> StoredDevices;
    public static ArrayList<DeviceModel> RoomDevices;
    public static ArrayList<BluetoothDevice> BLEDevices;
    public static Map<String, Integer> determineRoom;

    //Integers
    public static int DEFAULT_RSSI = -67;

    //Strings
    public static String currentRoom = "";

    //Methods
    private static String macAddress;

    public static void CheckDeviceNewbility(BluetoothDevice device, ScanResult result) {
        macAddress = device.getAddress();
        if (ScannedDevices.size() == 0 || !ScannedMacAddresses.contains(macAddress)) {
            ScannedMacAddresses.add(macAddress);
            DeviceModel model = new DeviceModel(macAddress, device.getName(), currentRoom, 0, 0, result.getScanRecord().getTxPowerLevel(), result.getRssi());
            ScannedDevices.add(model);
            BLEDevices.add(device);
//            connectToDeviceSelected(device);
        } else {
            for (DeviceModel model : ScannedDevices) {
                if (model.getAddress().equals(macAddress)) {
                    model.setRssi(result.getRssi());
//                    Log.e(">>>>>>>", model.getRssi() + "");
                }
            }
        }
    }


    public static void CheckDeviceSimilarity(BluetoothDevice device, ScanResult result) {
        macAddress = device.getAddress().trim();
        if (StoredDevices.size() > 0) {
            for (DeviceModel model : StoredDevices) {
                if (macAddress.equals(model.getAddress().trim())) {
                    model.setRssi(result.getRssi());
                    if (!RoomDevicesAddresses.contains(model.getAddress().trim())) {
                        RoomDevices.add(model);
                        RoomDevicesAddresses.add(model.getAddress().trim());
                    }
                    for (DeviceModel model1 : ScannedDevices) {
                        if (macAddress.equals(model1.getAddress().trim())) {
                            model1.setTx_power(model.getTx_power());
                        }
                    }
                }
            }
        }
    }

    public static void RemoveDevice(ArrayList<DeviceModel> devices, String macAddress) {
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getAddress().equals(macAddress)) {
                devices.remove(i);
            }
        }
    }

    private static void DetermineRoom() {
        for (DeviceModel model : RoomDevices) {
            if (determineRoom.containsKey(model.getRoom())) {
                determineRoom.put(model.getRoom(), determineRoom.get(model.getRoom()) + 1);
            } else {
                determineRoom.put(model.getRoom(), 1);
            }
        }
        currentRoom = RoomDevices.get(0).getRoom();
        for (Map.Entry<String, Integer> entry : determineRoom.entrySet()) {
            if (entry.getValue() > determineRoom.get(currentRoom)) {
                currentRoom = entry.getKey();
            }
        }
        for (DeviceModel model : RoomDevices) {
            if (!model.getRoom().equals(currentRoom)) {
                RemoveDevice(RoomDevices, model.getAddress());
            }
        }
    }

    private static float x1, x2, x3, y1, y2, y3;
    private static float r1, r2, r3;
    private static float A, B, C, D, E, F;
    private static float posx = 0, posy = 0;
    private static DeviceModel device1;
    private static DeviceModel device2;
    private static DeviceModel device3;

    private static float posxArr[] = {0f, 0f, 0f, 0f, 0f};
    private static float posyArr[] = {0f, 0f, 0f, 0f, 0f};
    private static int count = 0;
    private static float xAvg = 0;
    private static float yAvg = 0;

    public static Object[] CalPosition() {
        if (RoomDevices.size() > 0) {
            DetermineRoom();
            RoomModel model = getRoomAxis(currentRoom);
            if (model != null) {
                Log.e(">>>>>>>>>>>", RoomDevices.size() + "");
                if (RoomDevices.size() == 3) {
                    device1 = RoomDevices.get(0);
                    device2 = RoomDevices.get(1);
                    device3 = RoomDevices.get(2);
                    r1 = device1.getR();
                    r2 = device2.getR();
                    r3 = device3.getR();
                    x1 = device1.getPosx();
                    x2 = device2.getPosx();
                    x3 = device3.getPosx();
                    y1 = device1.getPosy();
                    y2 = device2.getPosy();
                    y3 = device3.getPosy();

//                    Log.e(">>>>x>>>>", x1 + ",,,," + x2 + ",,,," + x3);
//                    Log.e(">>>>y>>>>", y1 + ",,,," + y2 + ",,,," + y3);
//                    Log.e(">>>>r>>>>", r1 + ",,,," + r2 + ",,,," + r3);

                    A = -2 * x1 + 2 * x2;
                    B = -2 * y1 + 2 * y2;
                    C = (r1 * r1) - (r2 * r2) - (x1 * x1) + (x2 * x2) - (y1 * y1) + (y2 * y2);
                    D = -2 * x2 + 2 * x3;
                    E = -2 * y2 + 2 * y3;
                    F = (r2 * r2) - (r3 * r3) - (x2 * x2) + (x3 * x3) - (y2 * y2) + (y3 * y3);
//                    Log.e(">>>ABCDEF>>>", A + "  --  " + B + "  --  " + C + "  --  " + D + "  --  " + E + "  --  " + F);
                    posx = round(((C * E) - (F * B)) / ((A * E) - (D * B)));
                    posy = round(((C * D) - (F * A)) / ((B * D) - (E * A)));
//                    Log.e(">>>>>>>xy>>>>>", posx + ">>>>>>" + posy + ">>>>>>>>" + currentRoom);

                    if (posxArr[0] == 0) {
                        posxArr[0] = model.getPosx() / 2;
                    }
                    if (posyArr[0] == 0) {
                        posyArr[0] = model.getPosy() / 2;
                    }
                    if (posx > posxArr[count] + 0.2f) {
                        posx = posxArr[count] + 0.2f;
                        if (posx > model.getPosx()) {
                            posx = posxArr[count];
                        }
                    } else if (posx < posxArr[count] - 0.2f) {
                        posx = posxArr[count] - 0.2f;
                        if (posx < 0) {
                            posx = posxArr[count];
                        }
                    }
                    if (posy > posyArr[count] + 0.2f) {
                        posy = posyArr[count] + 0.2f;
                        if (posy > model.getPosy()) {
                            posy = posyArr[count];
                        }
                    } else if (posy < posyArr[count] - 0.2f) {
                        posy = posyArr[count] - 0.2f;
                        if (posy < 0) {
                            posy = posyArr[count];
                        }
                    }

                    if (posx == 0) {
                        posx = posxArr[count];
                    }
                    if (posy == 0) {
                        posy = posyArr[count];
                    }

//                    Log.e(">>>>>>>xy>>>>>", posx + ">>>>>>" + posy + ">>>>>>>>" + currentRoom);
                    count++;
                    if (count == 5) {
                        count = 0;
                    }
                    posxArr[count] = posx;
                    posyArr[count] = posy;

                    xAvg = 0;
                    yAvg = 0;
                    for (int i = 0; i < 5; i++) {
                        xAvg += posxArr[i];
                        yAvg += posyArr[i];
                    }
//                    Log.e(">>>>>avg>>>>>>>", xAvg + ">>>>>>" + yAvg + ">>>>>>>>" + currentRoom);
                    xAvg /= 5;
                    yAvg /= 5;


//                    Log.e(">>>>>>avg/5>>>>>>", xAvg + ">>>>>>" + yAvg + ">>>>>>>>" + currentRoom);
                    return new Object[]{round(xAvg), round(yAvg), currentRoom};
                }
            } else if (RoomDevices.size() < 3 && posx == 0 && posy == 0) {
//                    Log.e(">>>>>>>>>>>>", (model.getPosx() / 2f) + ">>>>>>" + (model.getPosy() / 2f) + ">>>>>>>>" + currentRoom);
                return new Object[]{model.getPosx() / 2f, model.getPosy() / 2f, currentRoom};
            }
        }

        return null;
    }

    private static RoomModel getRoomAxis(String room) {
        for (RoomModel model : Rooms) {
            if (model.getName().equals(room)) {
                return model;
            }
        }
        return null;
    }

    private static float round(float num) {
        return (int) ((num + 0.0005) * 1000f) / 1000f;
    }

}