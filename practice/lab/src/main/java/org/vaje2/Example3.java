package org.vaje2;

import util.Logger;

public class Example3 {
    public static void main(String[] args) throws InterruptedException {
        Logger.log("Example 3");

        Counter counter = new Counter();
        int count = 0;
        Thread t1 = new Thread(new CountingTask(counter));
        Thread t2 = new Thread(new CountingTask(counter));
        Thread t3 = new Thread(new CountingTask(counter));

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        Logger.log("Count at the end: "+ count);
    }
}
