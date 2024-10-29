package org.vaje4;

import util.LogLevel;
import util.Logger;

import java.awt.*;
import java.util.Random;

public class Example1 {

    public static final int NUM_OF_THREADS = 20;

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        long endTime = start + 7690;    //7690ms = optimalno za 20 threadou
        int totalHits = 0;
        int finalTotal = 0;

        DartThrower[] threads = new DartThrower[NUM_OF_THREADS];
        for (int i = 0; i < NUM_OF_THREADS; i++) {
            threads[i] = new DartThrower(endTime);
            threads[i].start();
            //threads[i].join();
        }

        for (DartThrower t : threads) {
            t.join();
            totalHits += t.getHits();
            finalTotal += t.getTotal();
        }


        double calculatedPI = 4. * totalHits / finalTotal;
        Logger.log("Calculated PI: " + calculatedPI, LogLevel.success);
        Logger.log("Error: " + (calculatedPI - Math.PI), LogLevel.error);
        Logger.log("Total: " + String.format("%,d", finalTotal));
    }
}
