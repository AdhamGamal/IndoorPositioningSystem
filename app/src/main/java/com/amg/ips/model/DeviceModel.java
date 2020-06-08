package com.amg.ips.model;


import static com.amg.ips.Utilities.N;
import static com.amg.ips.Utilities.W;

public class DeviceModel {
    private final String address;
    private final String name;
    private final String room;
    private final float posx;
    private final float posy;
    private int tx_power;
    private int rssi;
    private float r;
    private String uuid;

    public DeviceModel(String address, String name, String room, float posx, float posy, int tx_power, int rssi) {
        this.address = address;
        this.name = name;
        this.room = room;
        this.posx = posx;
        this.posy = posy;
        this.tx_power = tx_power;
        this.rssi = rssi;
        r = (float) Math.pow(10, ((tx_power - rssi + W) / (10 * N)));
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }

    public float getPosx() {
        return posx;
    }

    public float getPosy() {
        return posy;
    }

    public int getTx_power() {
        return tx_power;
    }

    public void setTx_power(int tx_power) {
        this.tx_power = tx_power;
    }


    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
        r = (float) Math.pow(10, ((tx_power - rssi + W) / (10 * N)));
    }

    public float getR() {
        return r;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
