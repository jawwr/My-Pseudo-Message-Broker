package com.jawwr.core;

import java.lang.reflect.Method;

public class Subscriber {
    private final String queueName;
    private final Method method;
    private final long time;

    public Subscriber(String queueName, Method method, long time) {
        this.queueName = queueName;
        this.method = method;
        this.time = time;
    }

    public void check() {
        if (QueuePool.isMessageExist(queueName)) {
            receiveMessage(this.method, this.queueName, this.time);
        }
    }

    private void receiveMessage(Method method, String queueName, long time) {
        Class<?> clazz = method.getDeclaringClass();
        Class<?> parameter = method.getParameterTypes()[0];
        String message = MessageBroker.receive(queueName, time);
        try {
            if (message != null) {
                var arg = MessageConverter.deserializeFromJson(message.getBytes(), parameter);
                method.invoke(clazz.getConstructor().newInstance(), arg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
