package com.jawwr;

import com.jawwr.core.annotations.BrokerSubscriber;
import com.jawwr.core.annotations.EnableBroker;
import com.jawwr.testEntity.Person;

@EnableBroker
public class Consumer2 {
    @BrokerSubscriber(queue = "queue2")
    private void receiver1(Person person){
        System.out.println();
    }
    @BrokerSubscriber(queue = "queue4")
    private void receiver2(Person person){
        System.out.println();
    }
}
