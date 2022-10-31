package com.jawwr;

import com.jawwr.core.MessageBroker;
import com.jawwr.testEntity.Person;

public class Application {
    public static void main(String[] args) throws InterruptedException {
        MessageBroker.run();

//        for (int i = 0; i < 100; i++){
//            MessageBroker.sendMessage("queue1", String.valueOf(i));
//        }


        Person person = new Person();
        person.setAge(19);
        person.setName("Who");
        person.setLastName("Where");

        MessageBroker.sendMessage("queue2", person);

        Thread.sleep(10_000);

        String message = MessageBroker.receive("queue2");
        System.out.println(message);

//        System.out.println();
    }
}
