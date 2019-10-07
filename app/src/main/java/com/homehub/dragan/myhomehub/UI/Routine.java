package com.homehub.dragan.myhomehub.UI;

public class Routine {
    private String DeviceName;

    public Routine(String deviceName){

        setDeviceName(deviceName);
    }
    public String getDeviceName() { return DeviceName; }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }
}
