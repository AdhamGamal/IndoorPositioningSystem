package com.amg.ips.ui;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import android.os.Bundle;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.amg.ips.R;
import com.google.android.material.tabs.TabLayout;

import static com.amg.ips.Utilities.*;
import static com.amg.ips.ui.PlaceholderFragment.*;

public class MainActivity extends AppCompatActivity {

    public static BluetoothManager btManager;
    public static BluetoothAdapter btAdapter;
    public static BluetoothLeScanner btScanner;
    public final static int REQUEST = 1;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.setCurrentItem(1);

        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST);
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST);
        }

        if (preferences == null) {
            preferences = getSharedPreferences(PREFERENCES_STORAGE, Context.MODE_PRIVATE);
            N = preferences.getFloat(PREFERENCES_N, 0);
            W = preferences.getFloat(PREFERENCES_W, 0);
            editor = preferences.edit();
        }

        ((ImageButton) findViewById(R.id.setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                View view = getLayoutInflater().inflate(R.layout.change_setting_dialog, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Setting");
                                final EditText N_EditText = (EditText) view.findViewById(R.id.N);
                                N_EditText.setText(preferences.getFloat(PREFERENCES_N, 0) + "");
                                final EditText W_EditText = (EditText) view.findViewById(R.id.W);
                                W_EditText.setText(preferences.getFloat(PREFERENCES_W, 0) + "");

                                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        editor.putFloat(PREFERENCES_N, Float.valueOf(N_EditText.getText().toString()));
                                        editor.putFloat(PREFERENCES_W, Float.valueOf(W_EditText.getText().toString()));
                                        editor.commit();
                                        N = preferences.getFloat(PREFERENCES_N, 0);
                                        W = preferences.getFloat(PREFERENCES_W, 0);
                                    }
                                });
                                builder.setNegativeButton(android.R.string.cancel, null);
                                builder.setView(view);
                                builder.show();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startScanning();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScanning();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Alert!");
                    builder.setMessage("Since location access has not been permitted, This app won't be able to discover beacons.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                }
            }
        }
    }
}
