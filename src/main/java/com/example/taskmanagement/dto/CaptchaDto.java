package com.example.taskmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CaptchaDto {
    private UUID secret;
    private String image;
}