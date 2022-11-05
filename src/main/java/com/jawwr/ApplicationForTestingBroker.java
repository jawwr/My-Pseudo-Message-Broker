package com.jawwr;

import com.jawwr.core.MessageBroker;
import com.jawwr.testEntity.Person;

public class ApplicationForTestingBroker {
    public static void main(String[] args) throws InterruptedException {
        MessageBroker.run();

        Person person = new Person();
        person.setAge(19);
        person.setName("Namee");
        person.setLastName("Last name");
//        for (int i = 0; i < 10; i++){
//            person.setId(i);
//            MessageBroker.sendMessage("queue2", person);
//        }
//        person.setLastName("Where");
//
//        MessageBroker.sendMessage("queue2", person);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(6_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < 10; i++){
                person.setId(i);
                MessageBroker.sendMessage("queue2", person);
            }
        });
        thread.start();
//        Thread.sleep(15_000);

        long startTime = System.currentTimeMillis();

        String message = MessageBroker.receive("queue2", 15_000L);

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
        System.out.println("from main: " + message);

//        System.out.println();
    }
}
