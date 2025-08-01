package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TCPDumpServer sets up a simple HTTP server, collects request metrics,
 * and the application window with two tabs:
 * - Attack Simulator Panel (client side)
 * - Monitoring & Visualization Panel (server side)
 */
public class TCPDumpServer {
    // shared counter for total requests
    private static final AtomicLong totalRequests = new AtomicLong(0);

    public static void main(String[] args) {
        try {
            startSimpleHttpServer();
            System.out.println("[INFO] HTTP server started on port 8080");
        } catch (IOException ex) {
            System.err.println("[ERROR] Failed to start HTTP server: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("DoS Detection Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Simulator", new AttackSimulatorPanel());
            tabs.addTab("Monitor", new MonitoringPanel());

            frame.add(tabs);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Starts a minimal HTTP server on port 8080 and increments totalRequests for each hit.
     */
    private static void startSimpleHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        HttpHandler handler = exchange -> {
            totalRequests.incrementAndGet();
            String response = "<html><body><h1>Welcome to DoS Test Server</h1></body></html>";
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        };
        server.createContext("/index.html", handler);
        server.createContext("/", exchange -> {
            exchange.getResponseHeaders().add("Location", "/index.html");
            exchange.sendResponseHeaders(302, -1);
            exchange.close();
            totalRequests.incrementAndGet();
        });
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
    }

    /**
     * Panel to simulate normal or DoS traffic using Apache Benchmark (ab).
     */
    static class AttackSimulatorPanel extends JPanel {
        private final JTextField urlField;
        private final JTextField concurrencyField;
        private final JTextField requestsField;
        private final JButton startButton;
        private final JButton stopButton;
        private final JTextArea outputArea;
        private Process currentProcess;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();

        public AttackSimulatorPanel() {
            setLayout(new BorderLayout(10, 10));
            JPanel input = new JPanel(new GridLayout(4, 2, 5, 5));
            input.add(new JLabel("Target URL:"));
            urlField = new JTextField("127.0.0.1:8080/index.html");
            input.add(urlField);
            input.add(new JLabel("Concurrency (-c):"));
            concurrencyField = new JTextField("100");
            input.add(concurrencyField);
            input.add(new JLabel("Total Requests (-n):"));
            requestsField = new JTextField("1000");
            input.add(requestsField);
            startButton = new JButton("Start");
            stopButton = new JButton("Stop");
            stopButton.setEnabled(false);
            input.add(startButton);
            input.add(stopButton);
            add(input, BorderLayout.NORTH);

            outputArea = new JTextArea();
            outputArea.setEditable(false);
            add(new JScrollPane(outputArea), BorderLayout.CENTER);

            startButton.addActionListener(e -> startBenchmark());
            stopButton.addActionListener(e -> stopBenchmark());
        }

        private void startBenchmark() {
            String raw = urlField.getText().trim();
            if (!raw.contains("/")) raw += "/";
            String toNormalize = raw.startsWith("http") ? raw : "http://" + raw;
            String targetUrl;
            try {
                URI uri = new URI(toNormalize);
                URL parsed = uri.toURL();
                String host = parsed.getHost();
                int port = parsed.getPort() != -1 ? parsed.getPort() : parsed.getDefaultPort();
                String path = parsed.getPath().isEmpty() ? "/" : parsed.getPath();
                String ip = InetAddress.getByName(host).getHostAddress();
                targetUrl = parsed.getProtocol() + "://" + ip + ":" + port + path;
            } catch (Exception ex) {
                outputArea.append("Invalid URL: " + ex.getMessage() + "\n");
                return;
            }
            SwingUtilities.invokeLater(() -> urlField.setText(targetUrl));
            List<String> cmdList = List.of("ab", "-c", concurrencyField.getText().trim(), "-n", requestsField.getText().trim(), targetUrl);
            String[] cmd = cmdList.toArray(new String[0]);
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            outputArea.setText("");
            executor.submit(() -> {
                try {
                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    pb.redirectErrorStream(true);
                    currentProcess = pb.start();
                    try (BufferedReader r = new BufferedReader(new InputStreamReader(currentProcess.getInputStream()))) {
                        String line;
                        while ((line = r.readLine()) != null) {
                            String out = line + System.lineSeparator();
                            SwingUtilities.invokeLater(() -> outputArea.append(out));
                        }
                    }
                    currentProcess.waitFor();
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> outputArea.append("Error running ab: " + ex.getMessage() + "\n"));
                } finally {
                    SwingUtilities.invokeLater(() -> { startButton.setEnabled(true); stopButton.setEnabled(false); });
                }
            });
        }

        private void stopBenchmark() {
            if (currentProcess != null) currentProcess.destroy();
            stopButton.setEnabled(false);
            startButton.setEnabled(true);
        }
    }

    /**
     * Panel to display real-time traffic metrics and a live chart using JFreeChart.
     */
    static class MonitoringPanel extends JPanel {
        private final JLabel statusLabel;
        private final JLabel throughputLabel;
        private final TimeSeries series;
        private long lastCount = 0;
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        public MonitoringPanel() {
            setLayout(new BorderLayout(10, 10));

            // Status bar
            JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            statusLabel = new JLabel("Status: Idle");
            throughputLabel = new JLabel("Throughput: 0 req/s");
            statusBar.add(statusLabel);
            statusBar.add(Box.createHorizontalStrut(20));
            statusBar.add(throughputLabel);
            add(statusBar, BorderLayout.NORTH);

            // Chart setup
            series = new TimeSeries("Requests/sec");
            TimeSeriesCollection dataset = new TimeSeriesCollection(series);
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    "Traffic Over Time",
                    "Time",
                    "Req/sec",
                    dataset,
                    false,
                    true,
                    false);
            ChartPanel chartPanel = new ChartPanel(chart);
            add(chartPanel, BorderLayout.CENTER);

            // Schedule sampling every second
            scheduler.scheduleAtFixedRate(this::sampleMetrics, 1, 1, TimeUnit.SECONDS);
        }

        private void sampleMetrics() {
            long current = totalRequests.get();
            long delta = current - lastCount;
            lastCount = current;
            double rps = delta; // per second
            SwingUtilities.invokeLater(() -> {
                throughputLabel.setText(String.format("Throughput: %.0f req/s", rps));
                series.addOrUpdate(new Millisecond(), rps);
            });
        }

        public void updateStatus(String status) {
            SwingUtilities.invokeLater(() -> statusLabel.setText("Status: " + status));
        }
    }
}