package com.example.taskmanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateDto {
    private String email;
    private String password;
}
