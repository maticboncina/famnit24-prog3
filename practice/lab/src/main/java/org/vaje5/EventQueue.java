package org.vaje5;

import util.Logger;

import java.util.LinkedList;

public class EventQueue {
    private LinkedList<Event> queue = new LinkedList<>();

    public synchronized int getSize(){
        return queue.size();
    }

    public synchronized void addEvent(Event event){
        queue.add(event);
        Logger.log("Event "+event.getEventType()+" added to the queue. Size "+queue.size());
    }

    public synchronized Event getEventIfType(EventType type){
        if(queue.isEmpty()) return null;
        if(queue.getFirst().getEventType() != type){
            return null;
        }

        Event event = queue.pop();
        Logger.log("Event "+event.getEventType()+" removed from queue. Size "+queue.size());
        return event;
    }
}