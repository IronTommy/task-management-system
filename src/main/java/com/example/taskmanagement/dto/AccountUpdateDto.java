package com.example.taskmanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountUpdateDto {
    private String firstName;
    private String lastName;

}
