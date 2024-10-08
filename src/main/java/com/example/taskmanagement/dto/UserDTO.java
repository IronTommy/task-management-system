package com.example.taskmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDTO {

    private UUID id;
    private String firstName;
    private String password;
    private String email;
}
