package com.jawwr;

import com.jawwr.core.MessageBroker;

public class Application {
    public static void main(String[] args) {
        MessageBroker.run();

        MessageBroker.sendMessage("queue1", "test");
        System.out.println();
    }
}
