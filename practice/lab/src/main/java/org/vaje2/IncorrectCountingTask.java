package org.vaje2;

import util.Logger;

public class IncorrectCountingTask implements Runnable{
    private int count;
    public IncorrectCountingTask(int c) {
        count=c;
    }
    @Override
    public void run() {
        Logger.log("Counting...");
        for (int i = 0; i < 100_000; i++) {
            count++;
        }
        Logger.log("Done counting...");
    }
}
