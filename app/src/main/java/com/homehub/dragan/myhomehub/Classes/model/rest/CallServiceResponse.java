package com.homehub.dragan.myhomehub.Classes.model.rest;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.homehub.dragan.myhomehub.Classes.model.Entity;

import java.util.ArrayList;

public class CallServiceResponse {

    @SerializedName("states")
    public ArrayList<Entity> states;

    public String toString() {
        return (new Gson()).toJson(this);
    }
}
