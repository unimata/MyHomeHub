package com.homehub.dragan.myhomehub;


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
