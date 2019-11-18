package com.homehub.dragan.myhomehub.UI;

import com.homehub.dragan.myhomehub.Classes.model.Entity;

public class Routine {


    private Entity linkedDevice;
    private String DeviceName;
    private String Action; //make this a different data type later
    private String Activator; //make this a different data type later
    private boolean isEnabled;

    public Routine(String deviceName, String action, String activator){

        // trying to figure out where to put linkedDevice.getFriendlyName()
        setDeviceName(deviceName);
        setAction(action);
        setActivator(activator);

    }
    public String getDeviceName() { return DeviceName; }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getAction() { return Action; }

    public void setAction(String action) { Action = action; }

    public String getActivator() { return Activator; }

    public void setActivator(String activator) { Activator = activator; }

    public Entity getLinkedDevice() { return linkedDevice; }

    public void setLinkedDevice(Entity linkedDevice) { this.linkedDevice = linkedDevice; }
}
