package com.example.jacksonobjectmapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class Pet {

    private String kind;
    private String name;
    private int age;
}

