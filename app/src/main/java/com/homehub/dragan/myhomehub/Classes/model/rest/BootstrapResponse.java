package com.homehub.dragan.myhomehub.Classes.model.rest;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.homehub.dragan.myhomehub.Classes.model.Config;
import com.homehub.dragan.myhomehub.Classes.model.Entity;
import com.homehub.dragan.myhomehub.Classes.model.Service;

import java.util.ArrayList;

public class BootstrapResponse {

    @SerializedName("config")
    public Config config;

//    @SerializedName("events")
//    public Event events;

    @SerializedName("services")
    public ArrayList<Service> services;

    @SerializedName("states")
    public ArrayList<Entity> states;


    public String toString()
    {
        return (new Gson()).toJson(this);
    }
}
