package com.example.taskmanagement.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class BaseDto implements Serializable {
    private UUID id;
    private Boolean isDeleted = false;
}
