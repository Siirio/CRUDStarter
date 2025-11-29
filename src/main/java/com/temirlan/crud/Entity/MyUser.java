package com.temirlan.crud.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;
    String surname;
    int age;
    @Column (unique = true, nullable = false)
    String email;


    public MyUser(int age, String name, String surname) {
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    //for tests
    public MyUser(Integer id, int age, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
    }
}
