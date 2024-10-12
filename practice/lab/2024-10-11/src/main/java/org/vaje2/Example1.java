package org.vaje2;

import util.LogLevel;
import util.Logger;

public class Example1 {
    public static void main(String[] args) throws InterruptedException {
        Logger.log("Example 1:", LogLevel.info);
        HelloThread myThread = new HelloThread();
        myThread.start();
        Thread anotherThread = new Thread(new HelloTask());
        anotherThread.start();

        myThread.join();
        anotherThread.join();

        Logger.log("done", LogLevel.success);
    }
}
