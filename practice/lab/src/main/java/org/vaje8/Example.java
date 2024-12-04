package org.vaje8;

public class Example {


    public static void main(String[] args) throws InterruptedException {
        new InputHandler().start();
        new Server().start();
        new ProtocolHandler().start();


        if (Constants.MY_IP.equals(Constants.BOOTSTRAP_IP)) {
            PeerList.connectToRemote(Constants.BOOTSTRAP_IP, Constants.PORT);
        }
    }
}
