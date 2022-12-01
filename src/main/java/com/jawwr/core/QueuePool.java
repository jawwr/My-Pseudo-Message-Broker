package com.jawwr.core;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public abstract class QueuePool {
    private QueuePool() {
    }

    private static final Map<String, Queue<byte[]>> queues = new HashMap<>();

    public static void addNewQueue(String name) {
        queues.put(name, new ArrayDeque<>());
    }

    public static Map<String, Queue<byte[]>> getQueues() {
        return queues;
    }

    public static void sendMessage(String key, byte[] message) {
        var queue = queues.get(key);
        if (queue == null) {
            queues.put(key, new ArrayDeque<>());
            queue = queues.get(key);
        }
        queue.add(message);
    }

    public static boolean isMessageExist(String key) {
        return queues.get(key).size() != 0;
    }

    public synchronized static byte[] getMessage(String key) {
        return queues.get(key).poll();
    }
}
