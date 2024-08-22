package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByAuthorId(UUID authorId, Pageable pageable);

    Page<Task> findByExecutorId(UUID executorId, Pageable pageable);

    Page<Task> findByAuthorIdOrExecutorId(UUID authorId, UUID executorId, Pageable pageable);

}
