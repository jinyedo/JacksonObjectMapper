package com.example.jacksonobjectmapper;

import lombok.*;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@ToString
public class User {

    private String name;
    private int age;
    private List<Pet> pets;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

