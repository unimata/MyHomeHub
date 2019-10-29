package com.homehub.dragan.myhomehub.UI;

public class Routine {
    private String DeviceName;
    private String Action; //make this a different data type later
    private String Activator; //make this a different data type later

    public Routine(String deviceName, String action, String activator){

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
}
