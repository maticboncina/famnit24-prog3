package org.vaje8;

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

        InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
        OutputStreamWriter streamWriter = new OutputStreamWriter(socket.getOutputStream());

        peerWriter = new BufferedWriter(streamWriter);
        peerReader = new BufferedReader(streamReader);

        PeerList.addPeer(this);
    }

    public String getIp() {
        return socket.getInetAddress().toString().replace("/", "");
    }


    public String waitForMessage() {
        try {
            return peerReader.readLine();
        } catch (IOException e) {
            Logger.log("Could not read message from peer: " + e.getMessage(), LogLevel.error);
            return null;
        }
    }

    public void sendMessage(Message message) {
        try {
            peerWriter.write(message.toString() + "\n");
            peerWriter.flush();
        } catch (IOException e) {
            Logger.log("Could not send message to peer..." + e.getMessage(), LogLevel.error);
        }
    }

    @Override
    public void run() {
        while (true) {

            String rawMessage = waitForMessage();
            if (rawMessage == null) {
                Logger.log("Connection to peer lost...", LogLevel.status);
                break;
            }

            Message message;
            try {
                message = new Message(rawMessage);
            } catch (IOException e) {
                Logger.log("Protocol violation: " + e.getMessage(), LogLevel.error);
                break;
            }

            TaskQueue.queue.add(new Task(this, message));
        }

        PeerList.removePeer(this);
        try {
            peerReader.close();
            peerWriter.close();
            socket.close();
        } catch (IOException e) {
            Logger.log("Something went wrong closing peer connection: " + e.getMessage(), LogLevel.error);
        }
    }
}
