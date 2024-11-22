package org.vaje6;

import util.Logger;

import java.util.Random;

public class Player implements Runnable {

    private Random r = new Random();
    private String name = "Player" + r.nextInt(100_000);
    private Game game = null;
    private boolean connected = true;


    @Override
    public void run() {
        Thread.currentThread().setName(name);
        Logger.log("Connected.");

        while (connected) {
            if (shouldJoinGame()) {
                game = Lobby.join(this);
            }
            if (shouldDisconnect()){
                if (game != null) {
                    game.leave(this);
                    game = null;
                }
                connected = false;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Logger.log("Disconnected.");
    }

    private boolean shouldJoinGame() {
        return game == null && 0.5 > r.nextDouble();
    }

    private boolean shouldDisconnect() {
        return 0.1 > r.nextDouble();
    }

}
