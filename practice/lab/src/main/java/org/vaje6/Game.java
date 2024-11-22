package org.vaje6;

import util.LogLevel;
import util.Logger;

import java.util.ArrayList;
import java.util.Random;

public class Game implements Runnable {

    private Random r = new Random();
    private String name = "Game" + r.nextInt(100_000);
    private ArrayList<Player> players = new ArrayList<>();

    public synchronized void join(Player player) {
        players.add(player);
    }

    public synchronized void leave(Player player) {
        players.remove(player);
    }

    private int playersLeft() {
        return players.size();
    }


    @Override
    public void run() {
        Thread.currentThread().setName(name);
        Logger.log("Starting...", LogLevel.status);

        while(playersLeft()>0) {
            Logger.log("Players left: " + playersLeft(), LogLevel.status);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Logger.log("Stopping...", LogLevel.status);
    }
}
