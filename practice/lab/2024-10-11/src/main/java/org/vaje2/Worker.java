package org.vaje2;

import util.Logger;

public class Worker extends Thread {
    private boolean running = true;
    @Override
    public void run() {
        Logger.log("Worker started working...");
        while(running){
            //System.out.println("Mining btc....");
        }
        Logger.log("Worker stopped working!");
    }
    public void setRunning(boolean running){
        this.running = running;
    }
}
