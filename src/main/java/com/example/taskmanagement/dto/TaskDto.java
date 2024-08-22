package com.example.taskmanagement.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TaskDto {
    private String title;
    private String description;
    private String status;
    private String priority;
    private UUID authorId;
    private UUID executorId;
}
