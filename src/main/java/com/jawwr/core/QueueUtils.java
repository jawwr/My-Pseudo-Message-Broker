package com.jawwr.core;

import com.jawwr.core.annotations.BrokerSubscriber;
import com.jawwr.core.annotations.EnableBroker;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class QueueUtils {
    public static List<Method> findSubscribers(List<Class<?>> consumers) {
        List<Method> subscribers = new ArrayList<>();
        for (Class<?> consumer : consumers) {
            Method[] methods = consumer.getDeclaredMethods();
            List<Method> methodList = Arrays.stream(methods)
                    .filter(method -> Arrays.stream(method.getDeclaredAnnotations())
                            .anyMatch(annotation -> annotation.annotationType() == BrokerSubscriber.class)).toList();
            subscribers.addAll(methodList);
        }
        return subscribers;
    }

    public static List<Class<?>> findConsumers() {
        Reflections reflections = new Reflections("", new SubTypesScanner(false));
        return reflections.getSubTypesOf(Object.class)
                .stream()
                .filter(clazz -> Arrays.stream(clazz.getDeclaredAnnotations())
                        .anyMatch(clazzAnn -> clazzAnn.annotationType() == EnableBroker.class))
                .collect(Collectors.toList());
    }

    public static void createQueue(List<Method> subscriber) {
        for (Method method : subscriber) {
            BrokerSubscriber ann = (BrokerSubscriber) Arrays.stream(method.getDeclaredAnnotations())
                    .filter(x -> x.annotationType() == BrokerSubscriber.class)
                    .findFirst()
                    .get();
            String queueName = ann.queue();
            QueuePool.addNewQueue(queueName);
        }
    }
}
