package com.homehub.dragan.myhomehub.Classes;

import com.homehub.dragan.myhomehub.Classes.HAConnectionEndPointEvent;
import com.homehub.dragan.myhomehub.Classes.HAEndPointSubscriber;
import com.homehub.dragan.myhomehub.Classes.HAWebSocketEndPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionHandler extends HAConnectionPublisher implements HAEndPointSubscriber, Runnable {
    public Thread thread; // will make private later
    //private List<HAConnectionEndPoint> connections = new ArrayList<>();
    HAWebSocketEndPoint haWebSocket;
    private boolean onlineStatus = false;
    private Lock eventLock = new ReentrantLock();
    private List<HAConnectionEndPointEvent> events = new ArrayList<>(); //needs a mutex
    private boolean stopThread;

    int pingTries = 0;
    long setupInterval = 0;
    int pingCounter = 0;
    private final static int PING_COUNT_MAX_RETRIES = 3;
    private final int WAIT_INTERVAL = 1000;


    public ConnectionHandler(){
        // Create a thread to run the connection handler on.
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void notifyChanged(HAConnectionEndPointEvent event) {
        eventLock.lock();
        events.add(event);
        eventLock.unlock();
        thread.interrupt();
    }

    @Override
    public void run() {
        System.out.println("ConnectionHandler Run");
        setup();
        final long standardSleepTime = 1000;
        final long standardWaitTime = 3000;
        long lastCheck = 0;

        long timeNow = 0;

        while(!stopThread){
            try {
                timeNow = System.currentTimeMillis();
                Thread.sleep(standardSleepTime);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                continue;

            }finally {
                checkMessages();
            }

            long diff = timeNow - lastCheck;
            if(lastCheck != 0 && diff < standardWaitTime) continue;
            lastCheck = System.currentTimeMillis();

            if(!onlineStatus){
                // System.out.println("SETUP!!!!");
                setup();
            }else{
                keepConnectionAliveOrShutDown();
            }

        }
        System.out.println("ConnectionHandler Stop");
    }

    private void keepConnectionAliveOrShutDown(){
        if(!onlineStatus) return;

        if(pingTries++ < PING_COUNT_MAX_RETRIES) {
            // System.out.println("trying to ping");
            haWebSocket.ping();
        }else {
            // System.out.println("We are bringing the endpoint down.");
            onlineStatus = false;
            pingTries = 0;
            haWebSocket.disconnect();
            haWebSocket = null;
            // notify subscribers
            HAConnectionEvent connectionEvent = new HAConnectionEvent();
            connectionEvent.eventType = "offline";
            publish(connectionEvent);
        }
    }

    private void checkMessages(){
        // Read events.
        eventLock.lock();
        for (HAConnectionEndPointEvent event: events) {
            switch (event.eventType){
                case "connected":
                    break;
                case "connecting":
                case "authenticating":
                    event.haConnectionEndPoint.authenticate();
                    break;
                case "online": {
                    onlineStatus = true;
                    setupInterval = 0;
                    // System.out.println("WE ARE ONLINE");
                    event.haConnectionEndPoint.registerSubscription();
                    event.haConnectionEndPoint.getStates();
                    event.haConnectionEndPoint.getGUILayout();

                    HAConnectionEvent connectionEvent = new HAConnectionEvent();
                    connectionEvent.eventObject = event.eventObject;
                    connectionEvent.eventType = event.eventType;
                    connectionEvent.eventStatus = event.eventStatus;
                    publish(connectionEvent);
                }
                    break;
                case "pong":{
                    // Do keep alive
                    pingTries = 0;
                    // System.out.println("PONG");
                }
                    break;
                case "event":
                case "get_states":
                case "lovelace_configuration": {
                    // Pass these messages the the subscribers.
                    HAConnectionEvent connectionEvent = new HAConnectionEvent();
                    connectionEvent.eventObject = event.eventObject;
                    connectionEvent.eventType = event.eventType;
                    connectionEvent.eventStatus = event.eventStatus;
                    publish(connectionEvent);
                }
                    break;
                default:
                        System.out.println("UNHANDLED");

            }
        }
        // Remove consumed events
        events.clear();
        eventLock.unlock();
    }

    public void shutdown(){
        System.out.println("shutting down!");
        stopThread = true;
        thread.interrupt();
    }

    private void setup(){
        if(onlineStatus) return;

        if(null != haWebSocket){
            haWebSocket.disconnect();
            haWebSocket = null;
        }
        haWebSocket = new HAWebSocketEndPoint();
        haWebSocket.subscribe(this);
        haWebSocket.connect();

    }

    public boolean addHAWebSocket(String url, String password){
        return false;

    }

    public void setState(HashMap<String, Object> object){
        haWebSocket.sendState(object);

    }
}
