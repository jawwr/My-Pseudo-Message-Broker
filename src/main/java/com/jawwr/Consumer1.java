package com.jawwr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jawwr.core.annotations.BrokerSubscriber;
import com.jawwr.core.annotations.EnableBroker;
import com.jawwr.testEntity.Person;

@EnableBroker
public class Consumer1 {
//    @BrokerSubscriber(queue = "queue2")
    public void receiver1(String s) {
        System.out.println("from 1: " + s);
    }

//    @BrokerSubscriber(queue = "queue2")
    public void receiver2(String s) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Person person = mapper.readValue(s, Person.class);
            System.out.println("from 2: " + person.getId());
        }catch (Exception e){
            e.printStackTrace();
        }
//        System.out.println("from 2: " + s);
    }
}