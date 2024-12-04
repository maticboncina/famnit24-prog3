package org.vaje8;

import util.LogLevel;
import util.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

public class PeerList {
    private static ArrayList<Peer> peers = new ArrayList<>();

    public static synchronized void addPeer(Peer peer) {
        peers.add(peer);
    }

    public static synchronized void removePeer(Peer peer) {
        peers.remove(peer);
    }

    public static synchronized void broadcast(Message message) {
        for (Peer p : peers) {
            p.sendMessage(message);
        }
    }

    public static synchronized String[] peerIPs(int max) {
        Collections.shuffle(peers);
        int peersToPull = Math.min(max, peers.size());
        String[] ips = new String[peersToPull];
        for (int i = 0 ; i < peersToPull ; i++) {
            ips[i] = peers.get(i).getIp();
        }
        return ips;
    }

    public static synchronized void connectToRemote(String ip, int port) {
        Socket socket;
        try {
            socket = new Socket(ip, port);
        } catch (IOException e) {
            Logger.log("Can't connect to remote peer: " + e.getMessage(), LogLevel.error);
            return;
        }

        Peer peer;
        try {
            peer = new Peer(socket);
        } catch (IOException e) {
            Logger.log("Could not run peer: " + e.getMessage(), LogLevel.error);
            return;
        }

        new Thread(peer).start();

        if (peers.size() < 2) {
            peer.sendMessage(new Message(MessageType.PEER_DISCOVERY_REQUEST, "2"));
        }
    }
}
