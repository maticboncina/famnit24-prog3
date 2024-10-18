package org.vaje2;

import util.Logger;

public class CountingTask implements Runnable{
    private Counter counter;
    public CountingTask(Counter count) {
        this.counter=count;
    }
    @Override
    public void run() {
        Logger.log("Counting...");
        for (int i = 0; i < 100_000; i++) {
            counter.count++;
        }
        Logger.log("Done counting...");
    }
}
