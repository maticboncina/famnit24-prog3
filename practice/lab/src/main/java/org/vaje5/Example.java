package org.vaje5;

import util.LogLevel;
import util.Logger;

public class Example {
    public static void main(String[] args) throws InterruptedException {
        Logger.log("producer consumer system");
        long startTime = System.currentTimeMillis();

        EventQueue queue = new EventQueue();
        Storage storage = new Storage();

        EventProducer producer = new EventProducer(queue);
        producer.start();

        CreateTicketConsumer createTicketConsumer = new CreateTicketConsumer(queue, storage);
        ValidateTicketConsumer validateTicketConsumer = new ValidateTicketConsumer(queue, storage);
        UseTicketConsumer useTicketConsumer = new UseTicketConsumer(queue, storage);

        createTicketConsumer.start();
        validateTicketConsumer.start();
        useTicketConsumer.start();

        producer.join();

        while (queue.getSize() > 0){
            Thread.sleep(50);
        }

        producer.join();

        long endTime = System.currentTimeMillis();
        Logger.log("Time: "+(endTime - startTime)+" ms", LogLevel.success);
    }
}
