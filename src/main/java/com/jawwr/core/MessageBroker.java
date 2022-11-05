package com.jawwr.core;

import com.jawwr.core.annotations.BrokerSubscriber;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageBroker {
    public static final Logger LOGGER = Logger.getLogger("Broker");

    public static void run() {
        var consumers = QueueUtils.findConsumers();
        LOGGER.log(Level.ALL, "[%s] Find all broker subscribers, total: %d");
        var subscriber = QueueUtils.findSubscribers(consumers);
        QueueUtils.createQueue(subscriber);
        subscribe(subscriber);
    }


    public static <T> void sendMessage(String key, T message) {
        byte[] buffer;
        try {
            buffer = MessageConverter.serializeToJson(message);
            QueuePool.sendMessage(key, buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void subscribe(List<Method> subscribers) {
        for (Method method : subscribers) {
            String queueName = getMethod(method).queue();
            long time = getMethod(method).time();
            Subscriber subscriber = new Subscriber(queueName, method, time);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    subscriber.check();
                }
            };
            Timer timer = new Timer(queueName);
            timer.scheduleAtFixedRate(timerTask, 30, 500);
        }
    }

    public static String receive(String key) {
        byte[] message = QueuePool.getMessage(key);
        if (message == null) {
            return null;
        }
        return new String(message);
    }

    public static String receive(String key, long times) {
        String message = null;
        for (int i = 0; (i * 500L) != times; ++i) {
            try {
                if (
                        QueuePool.getQueues().containsKey(key)
                                && QueuePool.getQueues().get(key).size() != 0
                ) {
                    message = receive(key);
                    if (message != null) {
                        break;
                    }
                }
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    public static String receive(String key, int seconds) {
        return receive(key, seconds * 1000L);
    }

    private static BrokerSubscriber getMethod(Method method) {
        return ((BrokerSubscriber) Arrays.stream(method.getDeclaredAnnotations())
                .filter(x -> x.annotationType() == BrokerSubscriber.class)
                .findFirst()
                .get());
    }
}