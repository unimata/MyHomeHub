package com.homehub.dragan.myhomehub.Classes;

import com.homehub.dragan.myhomehub.UI.SliderBasedControl;
import com.homehub.dragan.myhomehub.UI.SwitchBasedControl;


import java.lang.reflect.Array;

import java.util.ArrayList;

public final class DeviceList {
    private static final DeviceList list = new DeviceList();

    private ArrayList<Object> devices = new ArrayList<Object>();



    private DeviceList(){

        devices.add(new SliderBasedControl("Thermostat",23));
        devices.add(new SwitchBasedControl("Master Bedroom Lights", true));
        devices.add(new SwitchBasedControl("TV Backlighting", false));
        devices.add(new SliderBasedControl("Sonos",70));

        devices.add(true);//Keep this at the end always)
    }

    public static DeviceList getInstance(){
        return list;
    }

    public ArrayList<Object> getDevices(){
        return devices;
    }

}
