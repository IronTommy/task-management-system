package com.example.taskmanagement;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;
    private User executor;
    private Task task;

    @BeforeEach
    public void setUp() {
        author = new User();
        author.setFirstName("Author");
        author.setLastName("Lastname");
        author.setEmail("author@example.com");
        author.setPassword("password");
        author.setCreatedDate(ZonedDateTime.now());
        userRepository.save(author);

        executor = new User();
        executor.setFirstName("Executor");
        executor.setLastName("Lastname");
        executor.setEmail("executor@example.com");
        executor.setPassword("password");
        executor.setCreatedDate(ZonedDateTime.now());
        userRepository.save(executor);

        task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus("Open");
        task.setPriority("High");
        task.setAuthor(author);
        task.setExecutor(executor);
        taskRepository.save(task);
    }


    @Test
    public void testFindByAuthorId() {
        Page<Task> tasks = taskRepository.findByAuthorId(author.getId(), PageRequest.of(0, 10));
        assertFalse(tasks.isEmpty());
        assertEquals("Test Task", tasks.getContent().get(0).getTitle());
    }

    @Test
    public void testFindByExecutorId() {
        Page<Task> tasks = taskRepository.findByExecutorId(executor.getId(), PageRequest.of(0, 10));
        assertFalse(tasks.isEmpty());
        assertEquals("Test Task", tasks.getContent().get(0).getTitle());
    }

    @Test
    public void testFindByAuthorIdOrExecutorId() {
        Page<Task> tasks = taskRepository.findByAuthorIdOrExecutorId(author.getId(), executor.getId(), PageRequest.of(0, 10));
        assertFalse(tasks.isEmpty());
        assertEquals("Test Task", tasks.getContent().get(0).getTitle());
    }
}
