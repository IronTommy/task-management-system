package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.TaskDto;
import com.example.taskmanagement.dto.UserDTO;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.utils.auth.CurrentUserExtractor;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Task createTask(TaskDto taskDto) {

        UserDTO currentUserDTO = CurrentUserExtractor.getCurrentUserFromAuthentication();
        UUID userId = currentUserDTO.getId();
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + userId));

        if (taskDto.getExecutorId() == null) {
            throw new IllegalArgumentException("Executor ID must not be null");
        }

        User executor = getUserById(taskDto.getExecutorId());

        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());
        task.setAuthor(author);
        task.setExecutor(executor);

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, TaskDto taskDto) {
        Task task = getTaskById(id).orElseThrow(() -> new RuntimeException("Task not found"));

        UserDTO currentUserDTO = CurrentUserExtractor.getCurrentUserFromAuthentication();
        UUID userId = currentUserDTO.getId();
        if (!task.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("You can only edit your own tasks");
        }

        if (taskDto.getExecutorId() == null) {
            throw new IllegalArgumentException("Executor ID must not be null");
        }

        User executor = getUserById(taskDto.getExecutorId());
        task.setExecutor(executor);

        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setPriority(taskDto.getPriority());

        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id).orElseThrow(() -> new RuntimeException("Task not found"));

        UserDTO currentUserDTO = CurrentUserExtractor.getCurrentUserFromAuthentication();
        UUID userId = currentUserDTO.getId();
        if (!task.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own tasks");
        }

        taskRepository.deleteById(id);
    }

    public Optional<Task> getTaskById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Task ID must not be null");
        }
        return taskRepository.findById(id);
    }

    public Page<Task> getTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public User getUserById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Page<Task> getTasksByAuthor(UUID authorId, Pageable pageable) {
        return taskRepository.findByAuthorId(authorId, pageable);
    }

    public Page<Task> getTasksByExecutor(UUID executorId, Pageable pageable) {
        return taskRepository.findByExecutorId(executorId, pageable);
    }

    public Page<Task> getTasksByAuthorOrExecutor(UUID authorId, UUID executorId, Pageable pageable) {
        return taskRepository.findByAuthorIdOrExecutorId(authorId, executorId, pageable);
    }

    public Task findTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }


}






