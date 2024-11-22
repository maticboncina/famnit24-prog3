package org.vaje5;

import java.util.Random;

public class EventProducer extends Thread{
    private EventQueue eventQueue;

    public EventProducer(EventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    public void run(){
        Thread.currentThread().setName("EventProducer");
        Random r = new Random();
        EventType[] possibleEventTypes = EventType.values();

        for (int i = 0; i < 1_000; i++) {
            int ticketId = r.nextInt(100);
            int randomEventIndex = r.nextInt(possibleEventTypes.length);
            EventType randomEventType = possibleEventTypes[randomEventIndex];

            Event event = new Event(ticketId, randomEventType);
            eventQueue.addEvent(event);
        }
    }




}
