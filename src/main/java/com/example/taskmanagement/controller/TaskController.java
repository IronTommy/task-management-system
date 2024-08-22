package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.TaskDto;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskDto taskDto) {
        Task createdTask = taskService.createTask(taskDto);
        return ResponseEntity.ok(createdTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        Task updatedTask = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Task>> getTasks(Pageable pageable) {
        Page<Task> tasks = taskService.getTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.findTaskById(id);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/filter")
    public ResponseEntity<Page<Task>> getFilteredTasks(
            @RequestParam(required = false) UUID authorId,
            @RequestParam(required = false) UUID executorId,
            Pageable pageable) {
        Page<Task> tasks;
        if (authorId != null && executorId != null) {
            tasks = taskService.getTasksByAuthorOrExecutor(authorId, executorId, pageable);
        } else if (authorId != null) {
            tasks = taskService.getTasksByAuthor(authorId, pageable);
        } else if (executorId != null) {
            tasks = taskService.getTasksByExecutor(executorId, pageable);
        } else {
            tasks = taskService.getTasks(pageable);
        }
        return ResponseEntity.ok(tasks);
    }



}

