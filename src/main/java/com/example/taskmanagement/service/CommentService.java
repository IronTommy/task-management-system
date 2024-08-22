package com.example.taskmanagement.service;

import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskService taskService;

    public Comment createComment(String content, Long taskId, UUID authorId) {
        if (taskId == null) {
            throw new IllegalArgumentException("Task ID must not be null");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("Author ID must not be null");
        }

        Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User author = taskService.getUserById(authorId);

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setTask(task);
        comment.setAuthor(author);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTask(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("Task ID must not be null");
        }

        Task task = taskService.getTaskById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return commentRepository.findByTask(task);
    }

    public void deleteComment(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Comment ID must not be null");
        }
        commentRepository.deleteById(id);
    }
}


