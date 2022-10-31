package com.jawwr;

import com.jawwr.core.MessageBroker;
import com.jawwr.core.QueuePool;

public class Application {
    public static void main(String[] args) {
        MessageBroker.run();
        var cons = MessageBroker.getListConsumers();
        cons.forEach(x -> System.out.println("Consumers: " + x.getName()));

        var queues = QueuePool.getQueues();
        System.out.println("Queues:");
        for (String s : queues.keySet()) {
            System.out.println(s);
        }
    }
}
