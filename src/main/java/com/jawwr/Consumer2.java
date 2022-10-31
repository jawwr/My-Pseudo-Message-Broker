package com.jawwr;

import com.jawwr.core.annotations.BrokerSubscriber;
import com.jawwr.core.annotations.EnableBroker;
import com.jawwr.testEntity.Person;

@EnableBroker
public class Consumer2 {
    @BrokerSubscriber(queue = "queue1")
    public void receiver1(String s){
        System.out.println("from 2: " + s);
    }
    @BrokerSubscriber(queue = "queue4")
    public void receiver2(Person person){
        System.out.println();
    }
}
