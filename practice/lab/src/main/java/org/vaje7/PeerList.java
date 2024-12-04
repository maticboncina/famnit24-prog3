package org.vaje7;

import java.util.ArrayList;

public class PeerList {
    private static ArrayList<Peer> peers = new ArrayList<>();

    public synchronized static void addPeer(Peer peer) {
        peers.add(peer);
    }

    public synchronized static void removePeer(Peer peer) {
        peers.remove(peer);
    }

    public synchronized static void broadcast(String message) {
        for (Peer peer : peers) {
            peer.sendMessage(message);
        }
    }


}
