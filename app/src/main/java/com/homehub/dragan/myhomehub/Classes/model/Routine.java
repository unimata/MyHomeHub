package com.homehub.dragan.myhomehub.Classes.model;

import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.Trigger;

public class Routine {

    private Entity linkedDevice;
    private String DeviceName;
    private String Action; //make this a different data type later
    private String ActivatorName; //make this a different data type later

    public Entity triggerDevice;
    public String triggerAction;



    public int triggerDeviceNum;

    private Trigger trigger;
    private boolean isEnabled;

    public Routine(String deviceName, String action, String activator, Trigger trigger){

        // trying to figure out where to put linkedDevice.getFriendlyName()
        setDeviceName(deviceName);
        setAction(action);
        setActivatorName(activator);
        setTrigger(trigger);

        if (trigger.getIsTriggerOnDeviceAction() == true) {
            setTriggerDevice(triggerDevice);
            setTriggerAction(triggerAction);
            setTriggerDeviceNum(triggerDeviceNum);
        }

    }
    public String getDeviceName() { return DeviceName; }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getAction() { return Action; }

    public void setAction(String action) { Action = action; }

    public String getActivatorName() { return ActivatorName; }

    public void setActivatorName(String activator) { ActivatorName = activator; }

    public Entity getLinkedDevice() { return linkedDevice; }

    public void setLinkedDevice(Entity linkedDevice) { this.linkedDevice = linkedDevice; }

    public Trigger getTrigger() { return trigger; }

    public void setTrigger(Trigger trigger) { this.trigger = trigger; }

    public Entity getTriggerDevice() { return triggerDevice; }

    public void setTriggerDevice(Entity triggerDevice) { this.triggerDevice = triggerDevice; }

    public String getTriggerAction() { return triggerAction; }

    public void setTriggerAction(String triggerAction) { this.triggerAction = triggerAction; }

    public int getTriggerDeviceNum() { return triggerDeviceNum; }

    public void setTriggerDeviceNum(int triggerDeviceNum) { this.triggerDeviceNum = triggerDeviceNum; }
}
