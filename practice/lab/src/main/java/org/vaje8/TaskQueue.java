package org.vaje8;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskQueue {
    public static BlockingQueue<Task> queue = new LinkedBlockingQueue<>();
}
