package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.UserDTO;
import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.service.CommentService;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.utils.auth.CurrentUserExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestParam Long taskId, @RequestParam String content) {
        User currentUser = convertToUserEntity(CurrentUserExtractor.getCurrentUserFromAuthentication());
        Task task = taskService.getTaskById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setTask(task);
        comment.setAuthor(currentUser);
        Comment createdComment = commentService.createComment(comment);
        return ResponseEntity.ok(createdComment);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<Comment>> getCommentsByTask(@PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        List<Comment> comments = commentService.getCommentsByTask(task);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    private User convertToUserEntity(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}
