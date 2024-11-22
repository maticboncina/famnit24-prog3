package org.vaje5;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Storage {
    private ArrayList<Integer> tickets = new ArrayList<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

    public void addTicket(int ticket) {
        writeLock.lock();
        try {
            tickets.add(ticket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        tickets.add(ticket);
        writeLock.unlock();
    }

    public boolean ticketExists(int ticket) {
        readLock.lock();
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        boolean cont = tickets.contains(ticket);
        readLock.unlock();
        return cont;
    }

    public void removeTicket(int ticket) {
        writeLock.lock();
        try {
            Thread.sleep(10);
            tickets.remove((Integer) ticket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        writeLock.unlock();
    }
}
