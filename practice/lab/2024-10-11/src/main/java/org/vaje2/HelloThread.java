package org.vaje2;

import util.Logger;

public class HelloThread extends Thread {
    @Override
    public void run() {
        Thread.currentThread().setName("HelloThread");
        Logger.log("Hello from thread");
    }
}
