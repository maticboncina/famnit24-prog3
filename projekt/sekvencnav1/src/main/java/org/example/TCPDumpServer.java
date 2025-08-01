package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// navodila: make multi threaded, non parsers program. Make sure it does something with the data (counts req/minute)

public class TCPDumpServer {

    // tcpdump output queue
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    // Atomic counter to track requests
    private static final AtomicInteger requestCounter = new AtomicInteger(0);

    public static void main(String[] args) {
        // Start tcpdump thread
        Thread tcpDumpThread = new Thread(TCPDumpServer::runTcpDump);
        tcpDumpThread.start();

        // Start server thread
        Thread serverThread = new Thread(TCPDumpServer::runServer);
        serverThread.start();

        // Start logger thread
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(TCPDumpServer::logRequestsPerMinute, 0, 5, TimeUnit.SECONDS);
    }

    private static void runTcpDump() {
        String command = "tcpdump -l";
        Process process;

        try {
            // Start tcpdump process
            process = new ProcessBuilder(command.split(" ")).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                queue.offer(line);
            }

        } catch (IOException e) {
            System.err.println("Error running tcpdump: " + e.getMessage());
        }
    }

    private static void runServer() {
        int port = 8080; // 8080 like a true dockerer

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                // Accept incoming connections
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());

                // Start a new thread to handle the client
                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (socket) {
            // Increment the request counter
            requestCounter.incrementAndGet();

            // apache HTTP response template
            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: 19\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "\r\n" +
                    "Hello from Server!\n";

            socket.getOutputStream().write(httpResponse.getBytes());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private static void logRequestsPerMinute() {
        int requests = requestCounter.getAndSet(0); // countering
        String greenText = "\u001B[32m"; // make it green
        String resetText = "\u001B[0m"; // colorReset to white

        System.out.println(greenText + "Requests per minute: " + (requests * 12) + resetText); //some :star: fancy :star: math
    }
}
