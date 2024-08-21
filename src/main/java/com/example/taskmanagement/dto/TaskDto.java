package com.example.taskmanagement.dto;

import lombok.Data;

@Data
public class TaskDto {
    private String title;
    private String description;
    private String status;
    private String priority;
}
