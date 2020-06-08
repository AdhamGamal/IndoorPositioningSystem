package com.amg.ips.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.amg.ips.Utilities.*;

public class DeviceDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ipsapp.db";
    private static final int DATABASE_VERSION = 1;

    public DeviceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DEVICES_TABLE + " (" +
                DEVICE_MACADDRESS + " TEXT NOT NULL PRIMARY KEY, " +
                DEVICE_ROOM + " TEXT NOT NULL," +
                DEVICE_POSX + " FLOAT NOT NULL," +
                DEVICE_POSY + " FLOAT NOT NULL," +
                DEVICE_TX_POWER + " INT NOT NULL)");
        db.execSQL("CREATE TABLE " + ROOMS_TABLE + " (" +
                ROOM_NAME + " TEXT NOT NULL PRIMARY KEY," +
                ROOM_X + " FLOAT NOT NULL," +
                ROOM_Y + " FLOAT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DEVICES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ROOMS_TABLE);
        onCreate(db);
    }
}
