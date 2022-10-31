package com.jawwr.core;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public abstract class QueuePool {
    private static final Map<String, Queue<Byte[]>> queues = new HashMap<>();

    public static void addNewQueue(String name) {
        queues.put(name, new ArrayDeque<>());
    }

    public static Map<String, Queue<Byte[]>> getQueues() {
        return queues;
    }
    public static void sendMessage(String key, byte[] message){
        var queue = queues.get(key);
        Byte[] byteMessage = new Byte[message.length];
        for (int i = 0; i < message.length; i++) {
            byteMessage[i] = message[i];
        }
        queue.add(byteMessage);
    }
    public static boolean isMessageExist(String key){
        return queues.get(key).size() != 0;
    }
}
