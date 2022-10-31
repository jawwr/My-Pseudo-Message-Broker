package com.jawwr;

import com.jawwr.core.MessageBroker;
import com.jawwr.testEntity.Person;

public class Application {
    public static void main(String[] args) {
        MessageBroker.run();

        MessageBroker.sendMessage("queue1", "test");
        Person person = new Person();
        person.setAge(19);
        person.setName("Who");
        person.setLastName("Where");

        MessageBroker.sendMessage("queue2", person);

//        System.out.println();
    }
}
