package com.jawwr.testEntity;

public class Person {
    private int id;

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id + "," +
                "\"age\":" + age + "," +
                "\"name\":" + "\"" + name + "\"," +
                "\"lastName\":" + "\"" + lastName + "\"" +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    private Person() {
    }

    public Person(int age, String name, String lastName) {
        this.age = age;
        this.name = name;
        this.lastName = lastName;
    }

    public static class Builder{
        private int personAge;
        private String personName;
        private String personSurname;
        public Builder setAge(int age){
            this.personAge = age;
            return this;
        }
        public Builder setName(String name){
            this.personName = name;
            return this;
        }
        public Builder setSurname(String surname){
            this.personSurname = surname;
            return this;
        }

        public Person build(){
            Person person = new Person();
            person.age = this.personAge;
            person.name = this.personName;
            person.lastName = this.personSurname;

            return person;
        }
    }
}
