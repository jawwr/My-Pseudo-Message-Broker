package com.jawwr.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jawwr.core.annotations.BrokerSubscriber;
import com.jawwr.core.annotations.EnableBroker;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.ByteArrayOutputStream;
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
        subscribe(subscriber);
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

    public static <T> void sendMessage(String key, T message) {
        byte[] buffer;
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)
        ) {
            buffer = serializeToJson(message);
            QueuePool.sendMessage(key, buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> byte[] serializeToJson(T obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(obj);
    }

    private static void subscribe(List<Method> subscriber) {
        for (Method method : subscriber) {
            String queueName = getMethod(method).queue();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (QueuePool.isMessageExist(queueName)) {
                        receiveMessageBySubscriber(method, queueName);
                    }
                }
            };
            Timer timer = new Timer(queueName);
            timer.scheduleAtFixedRate(timerTask, 30, 500);
        }
    }

    private static void receiveMessageBySubscriber(Method method, String queueName) {
        Class<?> clazz = method.getDeclaringClass();
        Class<?> parameter = method.getParameterTypes()[0];
        try {
            String message = receive(queueName);
            var arg = deserializeFromJson(message.getBytes(), parameter);
            method.invoke(clazz.newInstance(), arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object deserializeFromJson(byte[] message, Class<?> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(message, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Not a deserializable object");
        }
    }

    public static String receive(String key) {
        byte[] message = QueuePool.getMessage(key);
        if (message == null) {
            return null;
        }
        return new String(message);
    }

    private static BrokerSubscriber getMethod(Method method) {
        return ((BrokerSubscriber) Arrays.stream(method.getDeclaredAnnotations())
                .filter(x -> x.annotationType() == BrokerSubscriber.class)
                .findFirst()
                .get());
    }
}
