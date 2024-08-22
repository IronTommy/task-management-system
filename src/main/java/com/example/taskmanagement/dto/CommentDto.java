package com.example.taskmanagement.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Long taskId;
    private String content;
}
