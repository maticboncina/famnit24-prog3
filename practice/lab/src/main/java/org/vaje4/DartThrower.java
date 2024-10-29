package org.vaje4;

import java.awt.*;

import java.util.Random;

public class DartThrower extends Thread {

    private long endTime;
    private int hits = 0;
    private int total = 0;

    public DartThrower(long endTime) {
        this.endTime = endTime;
    }


    @Override
    public void run() {
        Random r = new Random();
        while (System.currentTimeMillis() < endTime) {
            float x = r.nextFloat(-1, 1);
            float y = r.nextFloat(-1, 1);
            double xSquare = Math.pow(x, 2);
            double ySquare = Math.pow(y, 2);

            if (xSquare + ySquare <= 1) {
                hits++;
            }
            total++;
        }
    }

    public int getTotal() {
        return total;
    }

    public int getHits() {
        return hits;
    }
}
