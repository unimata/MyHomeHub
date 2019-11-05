package com.homehub.dragan.myhomehub.Classes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.homehub.dragan.myhomehub.Classes.HAHelpMethods;
import com.homehub.dragan.myhomehub.Classes.HAJSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;

public class HAWebSocketEndPoint extends HAEndPointPublisher implements HAConnectionEndPoint {
    private OkHttpClient mClient;
    private Request mRequest;
    private WebSocket mWebSocket;
    protected boolean connected = false;
    protected boolean online = false;
    private Integer requestId = 1;
    private HAWebSocketListener haWebSocketListener;
    private Integer latestGUIID = 0;
    private Integer latestStatesID = 0;
    private Integer latestSubscribeID = 0;

    private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJhMTJmZTNjMWRhZDI0MTNmYjgwMzQyMjg3Y2Q0OTM1ZSIsImlhdCI6MTU0OTcxMTMzOCwiZXhwIjoxODY1MDcxMzM4fQ.zIGF4j9yP3izTeZOaN1lPpScgImXVE-zdZ4TphfE9Lg";
    private static final String url = "wss://hass.exclude.se:8123/api/websocket";
    //private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiI3NmIzN2IzNmVkNTY0OGY3OTI3MDA2MWZlNmM2ODdjZSIsImlhdCI6MTU1NTQwODQzOSwiZXhwIjoxODcwNzY4NDM5fQ._pr7nlw_-ubp7Z5D6Bm81ObgRZS9YTe6-2c0tZCUwu8";
    //private static final String url = "ws://localhost:8123/api/websocket";

        private class HAWebSocketListener extends okhttp3.WebSocketListener {

        private HAWebSocketEndPoint haWebSocket;
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        public HAWebSocketListener(HAWebSocketEndPoint haWebSocket) {
            super();
            this.haWebSocket = haWebSocket;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response)
        {
            // System.out.println("CONNECTED");
            connected = true;
            haWebSocket.connected = true;
            HAConnectionEndPointEvent event = new HAConnectionEndPointEvent();
            event.eventType = "connected";
            event.eventStatus = "None";
            event.haConnectionEndPoint = haWebSocket;
            publish(event);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {

            //System.out.println("Receiving: " + text);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = (JsonObject) jsonParser.parse(text);
            JsonElement jsonElement = jsonParser.parse(text);
            HashMap<String,Object> parsedMsg = (HashMap<String,Object>) HAJSONParser.deserializeParse(jsonElement);
            // System.out.println("TEXT: " + text);

            String msgType = (String)parsedMsg.get("type");
            //if(msgType == null) return; // generate exception?

            switch (msgType){
                case "auth_required": {
                    HAConnectionEndPointEvent event = new HAConnectionEndPointEvent();
                    event.eventType = "authenticating";
                    event.eventStatus = "none";
                    event.haConnectionEndPoint = haWebSocket;
                    publish(event);
                    return;
                }

                case "auth_ok": {
                    HAConnectionEndPointEvent event = new HAConnectionEndPointEvent();
                    event.eventType = "online";
                    event.eventStatus = "success";
                    event.haConnectionEndPoint = haWebSocket;
                    publish(event);
                    return;
                }
                case "auth_invalid": {
                    HAConnectionEndPointEvent event = new HAConnectionEndPointEvent();
                    event.eventType = "online";
                    event.eventStatus = "failure";
                    event.haConnectionEndPoint = haWebSocket;
                    publish(event);
                    return;
                }
                case "pong": {
                    // Ping reply.
                    HAConnectionEndPointEvent event = new HAConnectionEndPointEvent();
                    event.eventType = "pong";
                    event.eventStatus = "none";
                    event.haConnectionEndPoint = haWebSocket;
                    publish(event);
                    return;
                }
                case "event":
                case "result":{
                    if(!connected) return;
                    int msgId = (int) parsedMsg.get("id");

                    if (msgId == latestGUIID){

                        HashMap<String, Object> result = (HashMap<String, Object>)parsedMsg.get("result");
                        if(null == result){return;}
                        List<Object> views = (List<Object>) result.get("views");
                        if(null == views) return;

                        HAConnectionEndPointEvent event = new HAConnectionEndPointEvent();
                        event.eventType = "lovelace_configuration";
                        event.eventStatus = "success";
                        event.eventObject = views;
                        event.haConnectionEndPoint = haWebSocket;
                        publish(event);

                    }else if (msgId == latestSubscribeID){
                        HashMap<String, Object> eventContent = (HashMap<String, Object>) parsedMsg.get("event");
                        if (eventContent == null) return;
                        String eventType = "-1";
                        try {
                            eventType = (String) eventContent.get("event_type");
                        } catch (ClassCastException e){
                            // this needs to be handled somehow!
                            return;
                        }

                        HAConnectionEndPointEvent event = new HAConnectionEndPointEvent();
                        switch (eventType) {
                            case "state_changed":
                            case "call_service":
                                event.eventType = "event";
                                break;
                            default:
                                System.out.println("Websocket: unhandled..");
                                return;
                        }

                        HashMap<String, Object> eventEvent = (HashMap<String, Object>) parsedMsg.get("event");
                        if(null == eventEvent) return; // ERROR?
                        HashMap<String, Object> eventData = (HashMap<String, Object>) eventEvent.get("data");
                        if(null == eventData) return; // ERROR?
                        HashMap<String, Object> eventNewState = (HashMap<String, Object>) eventData.get("new_state");
                        if(null == eventNewState) return ; //ERROR?

                        event.eventStatus = "changed";
                        event.haConnectionEndPoint = haWebSocket;
                        setStateType(eventNewState);
                        event.eventObject = eventNewState;
                        publish(event);

                    }else if (msgId == latestStatesID){
                        List<Object> valueList = (ArrayList<Object>)parsedMsg.get("result");
                        if(null == valueList) return;

                        HashMap<String, Object> configMap = new HashMap<>();
                        for(int i = 0 ; i < valueList.size(); ++i){
                            HashMap<String, Object> item = (HashMap<String, Object>) valueList.get(i);
                            if(null == item) {
                                // System.out.println("ITEM IN LIST... NOT working!!");
                                HashMap<String, Object> tmpItem = (HashMap<String, Object>) valueList.get(i);
                                System.out.println(tmpItem.toString());
                                continue;
                            }

                            String entitiyId = (String)item.get("entity_id");
                            if(null == entitiyId){
                                // System.out.println("THIS IS ERROR!");
                                continue;
                            }

                            configMap.put(entitiyId, item);
                        }

                        HAConnectionEndPointEvent event = new HAConnectionEndPointEvent();
                        event.eventType = "get_states";
                        event.eventStatus = "success";
                        event.haConnectionEndPoint = haWebSocket;
                        // loop through configMap and add type to them:
                        setMapStateType(configMap);

                        event.eventObject = configMap;
                        publish(event);
                    }else {
                        System.out.println("Config pushed, response: " + parsedMsg.toString());
                        //System.out.println("nothing matches..");
                        // This happens when we force a new state..
                    }
                }

                default:
                    break;
            }
        }

        private void setMapStateType(HashMap<String, Object> map) {
            // loop through configMap and add type to them:
            for(Map.Entry<String, Object> entry : map.entrySet())
            {
                HashMap<String, Object> value = (HashMap<String, Object>) entry.getValue();
                if(null == value) continue;
                HAHelpMethods.findStateType(entry.getKey());
                value.put("type", HAHelpMethods.findStateType(entry.getKey()));
            }
        }

        private void setStateType(HashMap<String, Object> item){
            String entity_id = (String) item.get("entity_id");
            item.put("type", HAHelpMethods.findStateType(entity_id));
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            System.out.println("--Receiving: " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            connected = false;
            haWebSocket.connected = false;
            haWebSocket.online = false;
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            System.out.println("Closing: " + code + " " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            // System.out.println("EPIC FAIL");
            haWebSocket.connected = false;
            connected = false;
            t.printStackTrace();
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            haWebSocket.online = false;
            haWebSocket.connected = false;
            connected = false;
            System.out.println("CLOSED");
        }
    }

    public HAWebSocketEndPoint(){ }


    @Override
    public void setup() {
        HAConnectionEndPointEvent event = new HAConnectionEndPointEvent();
        event.eventType = "setup";
        publish(event);
    }

    @Override
    public void authenticate(){
        if(!connected) return;
        String sendMe = "{\"type\": \"auth\", \"access_token\": \"" + password + "\"}";
        mWebSocket.send(sendMe);
    }

    @Override
    public void getStates(){
        if(!connected) return;
        latestStatesID = requestId;
        String sendMe = "{\"id\": " + (requestId++).toString() + ", \"type\": \"get_states\"}";
        mWebSocket.send(sendMe);
    }

    @Override
    public void getGUILayout(){
        if(!connected) return;
        latestGUIID = requestId;
        String sendMe = "{\"id\": " + (requestId++).toString() + ", \"type\": \"lovelace/config\"}";
        mWebSocket.send(sendMe);

    }

    @Override
    public void registerSubscription(){
        if(!connected) return;
        if(latestSubscribeID > 0){unregisterSubscription();}
        latestSubscribeID = requestId;
        String sendMe = "{\"id\": " + (requestId++).toString() + ", \"type\": \"subscribe_events\"}";
        mWebSocket.send(sendMe);

    }

    @Override
    public void unregisterSubscription(){
        if(!connected) return;
        String sendMe = "{\"id\": " + (requestId++).toString() + ", \"type\": \"unsubscribe_events\", \"subscription\": " +latestSubscribeID+ "}";
        mWebSocket.send(sendMe);
        latestSubscribeID = 0;
    }


    @Override
    public boolean isOnline() {
        return online;
    }

    @Override
    public void connect() {
        if(connected) {return;}
        requestId = 1;
        connected = false;
        mClient = new OkHttpClient();
        mRequest  = new Request.Builder().url(url).build();
        haWebSocketListener = new HAWebSocketListener(this);
        mWebSocket = mClient.newWebSocket(mRequest, haWebSocketListener);
    }

    @Override
    public void disconnect() {
        int NORMAL_CLOSURE_STATUS = 1000;
        if(!connected) return;

        mWebSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
        mClient.dispatcher().executorService().shutdown();
        mWebSocket = null;
        haWebSocketListener = null;
        mRequest = null;
        mClient = null;
        connected = false;
    }

    @Override
    public boolean testConfig(String url, String username, String password) {
        System.out.println("Testing url: " + url + ", username: " + username + ", password= " + password);
        return false;
    }
    @Override
    public void ping(){
        if(!connected) return;
        String sendMe = "{\"id\": " + (requestId++).toString()  +", \"type\": \"ping\" }";
        mWebSocket.send(sendMe);
    }

    @Override
    public void sendState(HashMap<String, Object> object) {
        JsonObject jsObj = (JsonObject) HAJSONParser.serializeParse(object);
        JsonObject sendObj = new JsonObject();
        sendObj.add("id", new JsonPrimitive((requestId++).toString()));
        sendObj.add("type", new JsonPrimitive("call_service"));
        sendObj.add("domain", new JsonPrimitive(jsObj.get("type").getAsString()));

        String service_state;
        String objState = (String)object.get("state");
        switch (objState){
            case "off":
                service_state = "turn_off";
                break;
            case "on":
                service_state = "turn_on";
                break;
            default:
                service_state = "";
                break;


        }
        sendObj.add("service", new JsonPrimitive(service_state));
        JsonObject service_data = new JsonObject();
        service_data.add("entity_id", jsObj.get("entity_id"));
        sendObj.add("service_data", service_data);
        mWebSocket.send(sendObj.toString());
    }
}
