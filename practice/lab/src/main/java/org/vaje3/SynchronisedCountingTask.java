package org.vaje3;

import util.LogLevel;
import util.Logger;

public class SynchronisedCountingTask implements Runnable {

    private SynchronizedCounter counter;

    public SynchronisedCountingTask(SynchronizedCounter counter) {
        this.counter = counter;
    }
    @Override
    public void run() {
        Logger.log("Incrementing...");
        for (int i = 0; i < 100_000; i++) {
            counter.increment();
        }
        Logger.log("Done..", LogLevel.success);
    }
}
