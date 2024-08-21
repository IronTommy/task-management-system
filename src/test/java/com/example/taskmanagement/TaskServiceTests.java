package com.example.taskmanagement;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@SpringBootTest
public class TaskServiceTests {

    @Autowired
    private TaskService taskService;

    @Test
    public void testCreateTask() {
        Task task = new Task();
        task.setTitle("Test Task");
        Task createdTask = taskService.createTask(task);
        assertNotNull(createdTask.getId());
    }

}

