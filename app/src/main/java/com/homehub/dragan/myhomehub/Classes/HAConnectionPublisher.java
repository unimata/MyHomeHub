package com.homehub.dragan.myhomehub.Classes;
import java.util.ArrayList;
import java.util.List;

abstract public class HAConnectionPublisher {
    private List<HAConnectionSubscriber> subscribers = new ArrayList<>();

    protected void publish(HAConnectionEvent event){
        List<Integer> nullSubscribers = new ArrayList<>();
        for(int i = 0 ; i < subscribers.size();++i){
            HAConnectionSubscriber subscriber = subscribers.get(i);
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
    public void subscribe(HAConnectionSubscriber subscriber){
        subscribers.add(subscriber);
    }
    public void unsubscribe(HAConnectionSubscriber subscriber){
        subscribers.remove(subscriber);
    }
}
