package org.vaje8;

import util.LogLevel;
import util.Logger;

import java.util.ArrayList;

public class ProtocolHandler extends Thread {

    private ArrayList<String> histroy = new ArrayList<>();


    @Override
    public void run() {
        while (true) {
            Task task;

            try {
                task = TaskQueue.queue.take();
            } catch (InterruptedException e) {
                Logger.log("Failed fetching task", LogLevel.error);
                continue;
            }


            if (histroy.contains(task.message.id)) {
                continue;
            }
            histroy.add(task.message.id);


            switch (task.message.type) {
                case CHAT -> handleChat(task);
                case PEER_DISCOVERY_REQUEST -> handlePeerDiscoveryRequest(task);
                case PEER_DISCOVERY_RESPONSE -> handlePeerDiscoveryResponse(task);
            }
        }
    }

    private void handlePeerDiscoveryResponse(Task task) {
        String[] ips = task.message.body.split(";");
        for (String ip : ips) {
            if (!ip.equals(Constants.MY_IP)) {
                PeerList.connectToRemote(ip, Constants.PORT);
            }
        }
    }

    private void handlePeerDiscoveryRequest(Task task) {
        int ipsToPull = Integer.parseInt(task.message.body);
        String[] ips = PeerList.peerIPs(ipsToPull);
        String body = String.join(";", ips);
        task.sender.sendMessage(new Message(
                MessageType.PEER_DISCOVERY_RESPONSE,
                body
        ));
    }

    private void handleChat(Task task) {
        Logger.log(task.message.body, LogLevel.success);
        PeerList.broadcast(task.message);
    }
}
