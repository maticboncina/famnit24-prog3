package org.vaje3;

import util.LogLevel;
import util.Logger;

public class exampleTwo {
    public static void main(String[] args) throws InterruptedException {
        Logger.log("Example Two");

        SynchronizedCounter counter = new SynchronizedCounter();

        Thread t1 = new Thread(new SynchronisedCountingTask(counter));
        Thread t2 = new Thread(new SynchronisedCountingTask(counter));
        Thread t3 = new Thread(new SynchronisedCountingTask(counter));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        Logger.log("Count at the end: "+counter.getCount(), LogLevel.success);
    }
}
