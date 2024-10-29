package org.vaje4;

import javax.swing.*;
import java.awt.*;

public class GUI {

    private JFrame frame;
    private JPanel panel;
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 800;

    public GUI() {
        frame = new JFrame("Calculating PI");
        panel = new JPanel();

        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(panel);
        frame.setVisible(true);
    }

    public void drawPoint(float x, float y, Color color) {
        Graphics2D gfx = (Graphics2D) panel.getGraphics();
        gfx.setColor(color);
        int translatedX = (int) (FRAME_WIDTH / 2 * x) + FRAME_WIDTH / 2;
        int translatedY = (int) (FRAME_HEIGHT / 2 * y) + FRAME_HEIGHT / 2;
        gfx.fillOval(translatedX, translatedY, 1, 1);
    }

    public void close() {
        frame.dispose();
    }

}
