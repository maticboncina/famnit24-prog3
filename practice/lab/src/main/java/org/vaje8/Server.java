package org.vaje8;

import util.LogLevel;
import util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    private ExecutorService peerExecutor = Executors.newCachedThreadPool();

    @Override
    public void run() {
        Thread.currentThread().setName("Server");
        ServerSocket ss;

        try {
            ss = new ServerSocket(Constants.PORT);
        } catch (IOException e) {
            Logger.log("Couldn't start server", LogLevel.error);
            return;
        }

        Logger.log("Server waiting for connections...", LogLevel.status);

        while (true) {
            Socket newPeer;

            try {
                newPeer = ss.accept();
            } catch (IOException e) {
                Logger.log("Couldn't establish connection with peer", LogLevel.error);
                continue;
            }
            Logger.log("---- New connection: ----");
            Logger.log("-> Local IP: " + newPeer.getLocalAddress());
            Logger.log("-> Local Port: " + newPeer.getLocalPort());
            Logger.log("-> Remote IP: " + newPeer.getInetAddress());
            Logger.log("-> Remote Port: " + newPeer.getPort());
            Logger.log("-------------------------");

            try {
                peerExecutor.submit(new Peer(newPeer));
            } catch (IOException e) {
                Logger.log("Something went wrong handling peer connection", LogLevel.error);
            }
        }
    }
}
