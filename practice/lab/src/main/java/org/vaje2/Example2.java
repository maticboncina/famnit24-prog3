package org.vaje2;

import util.Logger;

import java.util.Scanner;

public class Example2 {
    public static void main(String[] args) throws InterruptedException {
        Logger.log("Example 2");
        Worker worker = new Worker();
        worker.start();

        //wait to press enter
        new Scanner(System.in).nextLine();
        Logger.log("Stopping worker");
        worker.setRunning(false);

        worker.join();
        Logger.log("Done");
    }
}
