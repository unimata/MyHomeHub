package com.homehub.dragan.myhomehub.Classes.model;

import com.google.gson.annotations.SerializedName;

public class EventResponse {

    @SerializedName("id")
    public Integer id;

    @SerializedName("type")
    public String type;

    @SerializedName("event")
    public Event event;

    public class Event {
        @SerializedName("data")
        public EventData data;
    }

    public class EventData {
        @SerializedName("entity_id")
        public String entityId;

        @SerializedName("old_state")
        public Entity oldState;

        @SerializedName("new_state")
        public Entity newState;
    }
}