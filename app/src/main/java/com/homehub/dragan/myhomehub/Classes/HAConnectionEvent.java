package com.homehub.dragan.myhomehub.Classes;

public class HAConnectionEvent {
    public String eventType;
    public String eventStatus;
    public Object eventObject;

    @Override
    public String toString() {
        return "HAConnectionEndPointEvent{" +
                "eventType=" + eventType +
                ", eventStatus=" + eventStatus +
                ", eventObject=" + eventObject + "}";
    }

}
