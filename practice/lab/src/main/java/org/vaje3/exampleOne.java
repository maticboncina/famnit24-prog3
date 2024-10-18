package org.vaje3;

import org.vaje2.Counter;
import org.vaje2.CountingTask;
import util.LogLevel;
import util.Logger;

public class exampleOne {

    public static void main(String[] args) throws InterruptedException {
        Logger.log("Example 1");

        Counter counter = new Counter();

        Thread t1 = new Thread(new CountingTask(counter));
        Thread t2 = new Thread(new CountingTask(counter));
        Thread t3 = new Thread(new CountingTask(counter));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        Logger.log("Count at the end" + counter.count, LogLevel.success);
    }

}
