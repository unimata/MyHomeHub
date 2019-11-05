package com.homehub.dragan.myhomehub.Classes;

import com.google.gson.JsonObject;

import java.util.HashMap;

public interface HAConnectionEndPoint {
    public void setup();
    public boolean isOnline();
    public void connect();
    public void authenticate();
    public void getStates();
    public void getGUILayout();
    public void registerSubscription();
    public void unregisterSubscription();
    public void ping();
    public void disconnect();
    public boolean testConfig(String url, String username, String password);
    public void sendState(HashMap<String, Object> object);
}
