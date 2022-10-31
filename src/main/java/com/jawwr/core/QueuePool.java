package com.jawwr.core;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public abstract class QueuePool {
    private static final Map<String, Queue<?>> queues = new HashMap<>();

    public static void addNewQueue(String name) {
        queues.put(name, new ArrayDeque<>());
    }

    public static Map<String, Queue<?>> getQueues() {
        return queues;
    }
}
