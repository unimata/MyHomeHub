package com.homehub.dragan.myhomehub.Classes;

public class HAConnectionEndPointEvent {
    public String eventType;
    public String eventStatus;
    public Object eventObject;
    public HAConnectionEndPoint haConnectionEndPoint;

    @Override
    public String toString() {
        return "HAConnectionEndPointEvent{" +
                "eventType=" + eventType +
                ", eventStatus=" + eventStatus +
                ", eventObject=" + eventObject +
                ", haConnectionEndPoint=" + haConnectionEndPoint +
                '}';
    }

}
