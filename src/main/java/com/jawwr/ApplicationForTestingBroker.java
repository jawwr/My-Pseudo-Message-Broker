package com.jawwr;

import com.jawwr.core.MessageBroker;
import com.jawwr.testEntity.Person;

public class ApplicationForTestingBroker {
    public static void main(String[] args) throws InterruptedException {
        MessageBroker.run();

        Person person = new Person.Builder()
                .setAge(19)
                .setName("Name")
                .setSurname("Last name")
                .build();
//        person.setAge(19);
//        person.setName("Name");
//        person.setLastName("Last name");
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < 50; i++) {
                person.setId(i);
                MessageBroker.sendMessage("queue2", person.toString());
            }
        });
        thread.start();

        long startTime = System.currentTimeMillis();

        String message = MessageBroker.receive("queue2", 5_000L);

        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
        System.out.println("from main: " + message);
    }
}
