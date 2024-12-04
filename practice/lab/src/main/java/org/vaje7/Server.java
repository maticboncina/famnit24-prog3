package org.vaje7;

import util.LogLevel;
import util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {

    private ExecutorService executor = Executors.newCachedThreadPool();



    @Override
    public void run() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(7777);
        } catch (IOException e) {
            Logger.log("Could not listen on port: 7777: " + e.getMessage(), LogLevel.error);
            return;
        }

        Logger.log("Server listening on port 7777");

        while (true) {
            Socket newPeerConnection = null;

            try {
                newPeerConnection = serverSocket.accept();
            } catch (IOException e) {
                Logger.log("Could not accept new connection: " + e.getMessage(), LogLevel.error);
                continue;
            }

            Logger.log("-------- NEW CONNECTION: --------");
            Logger.log("-> Local IP: " + newPeerConnection.getLocalAddress());
            Logger.log("-> Local Port: " + newPeerConnection.getLocalPort());
            Logger.log("-> Remote IP: " + newPeerConnection.getInetAddress());
            Logger.log("-> Remote Port: " + newPeerConnection.getPort());
            Logger.log("---------------------------------");

            try {
                executor.submit(new Peer(newPeerConnection));
            } catch (IOException e) {
                Logger.log("Could not submit new connection: " + e.getMessage(), LogLevel.error);
            }
        }


    }
}
