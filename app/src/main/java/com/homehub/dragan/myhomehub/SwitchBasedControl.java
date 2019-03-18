package com.homehub.dragan.myhomehub;

import android.widget.Switch;

public class SwitchBasedControl {

    private String LinkedDeviceId;
    private boolean SwitchState;


    public SwitchBasedControl(String deviceId, boolean state){

        setLinkedDeviceId(deviceId);
        setSwitchState(state);

    }


    public boolean getSwitchState() {
        return SwitchState;
    }

    public void setSwitchState(boolean switchState) {
        SwitchState = switchState;
    }

    public String getLinkedDeviceId() {
        return LinkedDeviceId;
    }

    public void setLinkedDeviceId(String linkedDeviceId) {
        LinkedDeviceId = linkedDeviceId;
    }

}
