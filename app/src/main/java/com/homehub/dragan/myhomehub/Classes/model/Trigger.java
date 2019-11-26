package com.homehub.dragan.myhomehub.Classes.model;

public class Trigger {

    public boolean isTriggeredOnDeviceAction;

    public String triggerName;

    public Trigger(String triggerName) {
        setTriggerName(triggerName);
    }

    public boolean getIsTriggerOnDeviceAction() {
        return isTriggeredOnDeviceAction;
    }

    public void setIsTriggeredOnDeviceAction(boolean isTriggeredOnDeviceAction) { this.isTriggeredOnDeviceAction = isTriggeredOnDeviceAction; }

    public String getTriggerName() {
        return triggerName;
    }

    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

}
