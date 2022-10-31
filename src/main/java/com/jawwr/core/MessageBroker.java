package com.jawwr.core;

import com.jawwr.core.annotations.BrokerSubscriber;
import com.jawwr.core.annotations.EnableBroker;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MessageBroker {
    private static List<Class<?>> consumers;
    private static List<Method> subscriber;
    private static QueuePool pool;
    public static final Logger LOGGER = Logger.getLogger("Broker");

    public static void run() {
        consumers = findConsumers();
        LOGGER.log(Level.ALL, "[%s] Find all broker subscribers, total: %d");
        subscriber = findSubscribers();
        createQueue();
    }

    private static void createQueue() {
        for (Method method : subscriber) {
            BrokerSubscriber ann = (BrokerSubscriber) Arrays.stream(method.getDeclaredAnnotations())
                    .filter(x -> x.annotationType() == BrokerSubscriber.class)
                    .findFirst()
                    .get();
            String queueName = ann.queue();
            QueuePool.addNewQueue(queueName);
        }

    }

    private static List<Method> findSubscribers() {
        List<Method> subscribers = new ArrayList<>();
        for (Class<?> consumer : consumers) {
            Method[] methods = consumer.getDeclaredMethods();
            List<Method> methodList = Arrays.stream(methods)
                    .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                            .anyMatch(annotation -> annotation.annotationType() == BrokerSubscriber.class)).toList();
            subscribers.addAll(methodList);
        }
        LOGGER.log(Level.ALL, "[%s] Find all broker subscribers, total: %d", new Object[]{"Broker", subscribers.size()});
        return subscribers;
    }

    private static List<Class<?>> findConsumers() {
        Reflections reflections = new Reflections("", new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class)
                .stream()

                .filter(clazz -> Arrays.stream(clazz.getDeclaredAnnotations())
                        .anyMatch(clazzAnn -> clazzAnn.annotationType() == EnableBroker.class))
                .collect(Collectors.toList());
    }

    public static List<Class<?>> getListConsumers() {
        return consumers;
    }

    public static void sendMessage() {

    }
}
