package com.amg.ips.model;

public class RoomModel {
    private final String name;
    private final float posx;
    private final float posy;

    public RoomModel(String name, float posx, float posy) {
        this.name = name;
        this.posx = posx;
        this.posy = posy;
    }

    public String getName() {
        return name;
    }

    public float getPosx() {
        return posx;
    }

    public float getPosy() {
        return posy;
    }
}
