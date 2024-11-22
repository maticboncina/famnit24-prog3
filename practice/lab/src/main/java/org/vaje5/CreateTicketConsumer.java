package org.vaje5;

import util.LogLevel;
import util.Logger;

public class CreateTicketConsumer extends Thread {
    private EventQueue eventQueue;
    private Storage storage;

    public CreateTicketConsumer(EventQueue eventQueue, Storage storage) {
        this.eventQueue = eventQueue;
        this.storage = storage;
    }

   @Override
   public void run() {
        Thread.currentThread().setName("CreateTicketConsumer");

        while(true){
            Event eventToHandle = eventQueue.getEventIfType(EventType.CreateTicket);
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
