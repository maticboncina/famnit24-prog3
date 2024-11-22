package org.vaje5;

import util.LogLevel;
import util.Logger;

public class ValidateTicketConsumer extends Thread {
    private EventQueue eventQueue;
    private Storage storage;

    public ValidateTicketConsumer(EventQueue eventQueue, Storage storage) {
        this.eventQueue = eventQueue;
        this.storage = storage;
    }
    @Override
    public void run() {
        Thread.currentThread().setName("ValidateTicketConsumer");

        while(true){
            Event eventToHandle = eventQueue.getEventIfType(EventType.ValidateTicket);
            if (eventToHandle == null) {
                continue;
            }

            if(storage.ticketExists(eventToHandle.getTicketId())){
                Logger.log("Ticket Already Exists", LogLevel.warn);
                continue;
            }

            storage.addTicket(eventToHandle.getTicketId());
        }
    }
}
