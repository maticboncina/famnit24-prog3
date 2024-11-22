package org.vaje6;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Lobby {
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private static Game nextGame = new Game();
    private static Object nextGameLock = new Object();
    private static CyclicBarrier barrier = new CyclicBarrier(4, () -> {
        synchronized (nextGameLock) {
            executor.submit(nextGame);
            nextGame = new Game();
        }
    });

    public static Game join(Player player) {
        Game game = nextGame;
        synchronized (nextGameLock) {
            game.join(player);
        }

        try {
            barrier.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (BrokenBarrierException e) {
            throw new RuntimeException(e);
        }
        return game;
    }
}
