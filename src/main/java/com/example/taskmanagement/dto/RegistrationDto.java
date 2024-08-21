package com.example.taskmanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RegistrationDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password1;
    private String password2;
}
