package org.vaje7;

import util.Logger;

import java.io.IOException;
import java.net.ServerSocket;

public class Example {
    public static void main(String[] args) throws IOException {
        new Server().start();
        new UserInput().start();
    }
}
