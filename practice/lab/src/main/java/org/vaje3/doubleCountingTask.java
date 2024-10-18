package org.vaje3;

import util.LogLevel;
import util.Logger;

public class doubleCountingTask implements Runnable {

    private doubleCounter counter;
    private counterSelector selector;

    public doubleCountingTask(doubleCounter counter, counterSelector selector) {
        this.counter = counter;
        this.selector = selector;
    }

    @Override
    public void run() {
        Logger.log("Incrementing...");
        for (int i = 0; i < 100_000; i++) {
            if (selector == counterSelector.first){
                counter.incrementC1();
            }

            if (selector == counterSelector.second){
                counter.incrementC2();
            }
        }

        Logger.log("Done...", LogLevel.success);
    }
}
