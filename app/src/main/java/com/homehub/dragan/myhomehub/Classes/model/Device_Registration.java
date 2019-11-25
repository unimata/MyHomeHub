package com.homehub.dragan.myhomehub.Classes.model;

public class Device_Registration {
    private String device;
    private String device_name;

    public Device_Registration() {

    }

    public Device_Registration(String device, String device_name) {
        this.device = device;
        this.device_name = device_name;
    }

    public String getDevice_Name() {
        return device_name;
    }

    public void setDevice_Name(String device_name) {
        this.device_name = device_name;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }


}
