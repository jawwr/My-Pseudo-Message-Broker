package com.jawwr;

import com.jawwr.core.annotations.BrokerSubscriber;
import com.jawwr.core.annotations.EnableBroker;

@EnableBroker
public class Consumer1 {
    @BrokerSubscriber(queue = "queue2")
    public void receiver1(String s) {
        System.out.println("from 1: " + s);
    }

    @BrokerSubscriber(queue = "queue2")
    public void receiver3(String s) {
        System.out.println("from 3: " + s);
    }

    @BrokerSubscriber(queue = "queue2")
    public void receiver4(String s) {
        System.out.println("from 4: " + s);
    }

    //    @BrokerSubscriber(queue = "queue2")
    public void receiver5(String s) {
        System.out.println("from 5: " + s);
    }

    @BrokerSubscriber(queue = "queue2")
    public void receiver2(String s) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            Person person = mapper.readValue(s, Person.class);
//            System.out.println("from 2: " + person.getId());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        System.out.println("from 2: " + s);
    }
}