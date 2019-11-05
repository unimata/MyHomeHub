package com.homehub.dragan.myhomehub.Classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class HomeHub implements HAConnectionSubscriber, Runnable {
    private static HomeHub ourInstance = new HomeHub();
    public Thread thread;
    private boolean connected = false;
    private boolean stopThread = false;
    private ConnectionHandler connectionHandler;
    private Lock eventLock = new ReentrantLock();
    private List<HAConnectionEvent> events = new ArrayList<>(); //needs a mutex
    private HashMap<String, Object> HAItems;
    private ArrayList<HashMap<Integer, Object>> GUIItems;

    private HomeHub() {
        thread = new Thread(this);
        thread.start();
        connectionHandler = new ConnectionHandler();
        connectionHandler.subscribe(this);

    }
    public void startThread(){
        if(null != thread) return;
        this.stopThread = false;
        thread.start();
    }

    public void stopThread() {
        this.stopThread = true;
        this.thread.interrupt();

    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }


    public static HomeHub getInstance() {
        return ourInstance;
    }


    public void run() {
        System.out.println("Started new thread");

        while (!stopThread){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){
            }
            finally {
                checkMessages();
            }
        }

    }

    private void checkMessages(){
        eventLock.lock();
        // do stuff!
        for (HAConnectionEvent event: events)
        {
            switch (event.eventType){
                case "online":{
                    connected = true;
                    System.out.println("HA: we are online");
                }
                break;
                case "offline":{
                    connected = false;
                    System.out.println("HA: we are offline");
                }
                break;
                case "get_states":{
                    System.out.println("HA: states");
                    HAItems = (HashMap<String, Object>) event.eventObject;
                }
                break;
                case "lovelace_configuration":{
                    System.out.println("HA: lovelace");
                    GUIItems = (ArrayList<HashMap<Integer, Object>>)  event.eventObject;
                }
                break;
                case "event":{
                    System.out.println("HA: new event");
                    System.out.println(event.toString());

                    HashMap<String, Object> eventObjectType  = (HashMap<String, Object>) event.eventObject;
                    if(null == eventObjectType) break;

                    String objectName = (String) eventObjectType.get("string");
                    if(null == objectName) break;

                    HashMap<String, Object> itemObject = (HashMap<String, Object>) HAItems.get(objectName);
                    if(null == itemObject) break;

                    System.out.println("HA: Saved new state.");
                    itemObject = eventObjectType; // saved the new state
                    System.out.println(itemObject);


                }

                break;

            }
            // System.out.println(event);
        }

        // Remove consumed events
        events.clear();
        eventLock.unlock();
    }

    @Override
    public void notifyChanged(HAConnectionEvent event) {
        eventLock.lock();
        events.add(event);
        eventLock.unlock();
        thread.interrupt();
    }

    public HashMap<String, Object> getAllStates(){
        return (HashMap<String, Object>)HAItems.clone();
    }
    public HashMap<String, Object> getState(String state){
        return (HashMap<String, Object>) HAItems.get(state);

    }
    public void setState(HashMap<String, Object> object){
        //HashMap<String, Object> newAttributes = (HashMap<String, Object>)HAItems.get(objectName);
        //newAttributes = object;
        connectionHandler.setState(object);
        System.out.println("SETTING STATE");
    }


}
