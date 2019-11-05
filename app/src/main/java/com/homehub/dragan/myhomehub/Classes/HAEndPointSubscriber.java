package com.homehub.dragan.myhomehub.Classes;

public interface HAEndPointSubscriber {
    void notifyChanged(HAConnectionEndPointEvent event);
}
