package com.amg.ips;

import android.net.Uri;

public class Utilities {

    //Database
    public static final String DEVICES_TABLE = "devices";
    public static final String ROOMS_TABLE = "rooms";

    private static final String AUTHORITY = "com.amg.ipsapp";

    public static final Uri DEVICES_BASE_URI = Uri.parse("content://" + AUTHORITY + "/" + DEVICES_TABLE);
    public static final Uri ROOMS_BASE_URI = Uri.parse("content://" + AUTHORITY + "/" + ROOMS_TABLE);

    public static final String DEVICE_MACADDRESS = "device_macaddress";
    public static final String DEVICE_ROOM = "device_room";
    public static final String DEVICE_POSX = "device_posx";
    public static final String DEVICE_POSY = "device_posy";
    public static final String DEVICE_TX_POWER = "device_tx_power";

    public static final String ROOM_NAME = "room_name";
    public static final String ROOM_X = "room_x";
    public static final String ROOM_Y = "room_y";


    public static final String[] DEVICE_COLUMNS = {
            DEVICE_MACADDRESS,
            DEVICE_ROOM,
            DEVICE_POSX,
            DEVICE_POSY,
            DEVICE_TX_POWER
    };
    public static final String[] ROOM_COLUMNS = {
            ROOM_NAME,
            ROOM_X,
            ROOM_Y
    };

    //Extras Keys
    public static final String INDEX = "INDEX";
    public static final String DEVICE_NO_KEY = "device";
    public static final String ROOM_NO_KEY = "room";


    //Prefrances
    public static final String PREFERENCES_STORAGE = "preferences";
    public static final String PREFERENCES_N = "N";
    public static final String PREFERENCES_W = "W";
    public static float N = 0;
    public static float W = 0;



}
