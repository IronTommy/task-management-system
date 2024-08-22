package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.CommentDto;
import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.service.CommentService;
import com.example.taskmanagement.utils.auth.CurrentUserExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody CommentDto commentDto) {
        UUID authorId = CurrentUserExtractor.getCurrentUserFromAuthentication().getId();
        Comment createdComment = commentService.createComment(commentDto.getContent(), commentDto.getTaskId(), authorId);
        return ResponseEntity.ok(createdComment);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<Comment>> getCommentsByTask(@PathVariable Long taskId) {
        List<Comment> comments = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

