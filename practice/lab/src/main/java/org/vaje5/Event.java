package org.vaje5;

public class Event {
    private int ticketId;
    private EventType eventType;

    public Event(int ticketId, EventType eventType) {
        this.ticketId = ticketId;
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }

    public int getTicketId() {
        return ticketId;
    }
}
