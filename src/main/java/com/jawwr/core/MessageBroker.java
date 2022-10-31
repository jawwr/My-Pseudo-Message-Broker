package com.jawwr.core;

import com.jawwr.core.annotations.BrokerSubscriber;
import com.jawwr.core.annotations.EnableBroker;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MessageBroker {
    public static final Logger LOGGER = Logger.getLogger("Broker");

    public static void run() {
        var consumers = findConsumers();
        LOGGER.log(Level.ALL, "[%s] Find all broker subscribers, total: %d");
        var subscriber = findSubscribers(consumers);
        createQueue(subscriber);
        startTimer(subscriber);
    }

    private static void createQueue(List<Method> subscriber) {
        for (Method method : subscriber) {
            BrokerSubscriber ann = (BrokerSubscriber) Arrays.stream(method.getDeclaredAnnotations())
                    .filter(x -> x.annotationType() == BrokerSubscriber.class)
                    .findFirst()
                    .get();
            String queueName = ann.queue();
            QueuePool.addNewQueue(queueName);
        }
    }

    private static List<Method> findSubscribers(List<Class<?>> consumers) {
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

    public static void sendMessage(String key, Object message) {
        byte[] buffer;
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            oos.writeObject(message);
            buffer = baos.toByteArray();
            QueuePool.sendMessage(key, buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startTimer(List<Method> subscriber) {
            for (Method method : subscriber) {
                String queueName = ((BrokerSubscriber) Arrays.stream(method.getDeclaredAnnotations())
                        .filter(x -> x.annotationType() == BrokerSubscriber.class)
                        .findFirst()
                        .get()).queue();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (QueuePool.isMessageExist(queueName)) {
                            Class<?> parameterType = method.getParameterTypes()[0];
                            Class<?> clazz = method.getDeclaringClass();
                            byte[] message = QueuePool.getMessage(queueName);
                            try {
                                var obj = new ObjectInputStream(new ByteArrayInputStream(message)).readObject();
                                method.invoke(clazz.newInstance(), obj);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Timer timer = new Timer(queueName);
                timer.scheduleAtFixedRate(timerTask, 30, 500);
            }
            System.out.println();
    }
}
