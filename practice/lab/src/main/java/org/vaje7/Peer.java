package org.vaje7;

import util.LogLevel;
import util.Logger;

import java.io.*;
import java.net.Socket;

public class Peer implements Runnable {
    private Socket socket;
    private BufferedReader peerReader;
    private BufferedWriter peerWriter;

    public Peer(Socket socket) throws IOException {
        this.socket = socket;

        InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream();

        InputStreamReader isr = new InputStreamReader(is);
        OutputStreamWriter osr = new OutputStreamWriter(os);

        peerReader = new BufferedReader(isr);
        peerWriter = new BufferedWriter(osr);
    }

    private String waitForMessage() {
        try {
            return peerReader.readLine();
        } catch (IOException e) {
            Logger.log("Could not read message: " + e.getMessage(), LogLevel.error);
            return null;
        }
    }

    public void sendMessage(String message) {
        try {
            peerWriter.write(message + "\n");
            peerWriter.flush();
        } catch (IOException e) {
            Logger.log("Could not write message: " + e.getMessage(), LogLevel.error);
        }

    }

    @Override
    public void run() {

        PeerList.addPeer(this);

        while (true) {
            String message = waitForMessage();
            if (message == null) {
                Logger.log("Peer disconnected", LogLevel.warn);
                break;
            }

            Logger.log(message, LogLevel.status);
        }

        PeerList.removePeer(this);
    }
}
