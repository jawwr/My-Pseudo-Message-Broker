package com.jawwr.testEntity;

public class Person {
    private int age;
    private String name;
    private String lastName;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Person() {
    }

    public Person(int age, String name, String lastName) {
        this.age = age;
        this.name = name;
        this.lastName = lastName;
    }
}
