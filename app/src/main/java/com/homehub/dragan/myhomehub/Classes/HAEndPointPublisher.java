package com.homehub.dragan.myhomehub.Classes;

import java.util.ArrayList;
import java.util.List;

abstract public class HAEndPointPublisher {
    private List<HAEndPointSubscriber> subscribers = new ArrayList<>();

    protected void publish(HAConnectionEndPointEvent event){
        List<Integer> nullSubscribers = new ArrayList<>();
        for(int i = 0 ; i < subscribers.size();++i){
            HAEndPointSubscriber subscriber = subscribers.get(i);
            // if the subscriber is null, remove it from the list.
            if(subscriber != null){
                subscriber.notifyChanged(event);
            }else{
                nullSubscribers.add(i);
            }
        }
        // Remove if there is null subscribers
        for (int i = 0 ; i < nullSubscribers.size(); ++i){
            subscribers.remove(nullSubscribers.get(i));
        }

    }
    public void subscribe(HAEndPointSubscriber subscriber){
        subscribers.add(subscriber);
    }
    public void unsubscribe(HAEndPointSubscriber subscriber){
        subscribers.remove(subscriber);
    }
}
