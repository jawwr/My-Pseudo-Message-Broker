package com.jawwr;

import com.jawwr.core.annotations.BrokerSubscriber;
import com.jawwr.core.annotations.EnableBroker;
import com.jawwr.testEntity.Person;

@EnableBroker
public class Consumer1 {
    @BrokerSubscriber(queue = "queue1")
    public void receiver1(String s){
        System.out.println(s);
    }
    @BrokerSubscriber(queue = "queue2")
    public void receiver2(Person person){
        System.out.println(person.getAge());
    }
}
