package org.vaje8;

import java.util.Scanner;

public class InputHandler extends Thread {

    @Override
    public void run() {
        Scanner s = new Scanner(System.in);
        while (true) {
            String input = s.nextLine();
            PeerList.broadcast(new Message(MessageType.CHAT, input));
        }
    }
}
