package com.temirlan.crud.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyUserDTO {
    Integer id;
    String name;
    String surname;
    int age;
    String email;
}