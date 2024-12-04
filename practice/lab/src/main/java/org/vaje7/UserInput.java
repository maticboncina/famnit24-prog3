package org.vaje7;

import java.util.Scanner;

public class UserInput extends Thread {
    @Override
    public void run() {
        Scanner s = new Scanner(System.in);
        while (true) {
            String input = s.nextLine();
            PeerList.broadcast(input);
        }
    }
}
