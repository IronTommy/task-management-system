package com.example.taskmanagement;

import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private Task task;
    private User author;

    @BeforeEach
    public void setUp() {
        author = new User();
        author.setFirstName("AuthorFirstName");
        author.setLastName("AuthorLastName");
        author.setEmail("author@example.com");
        author.setPassword("password");
        author.setCreatedDate(ZonedDateTime.now());
        userRepository.save(author);

        task = new Task();
        task.setTitle("Test Task");
        task.setStatus("OPEN");
        task.setPriority("MEDIUM");
        task.setAuthor(author);
        taskRepository.save(task);
    }

    @Test
    public void testFindByTask() {
        Comment comment = new Comment();
        comment.setContent("Test Comment");
        comment.setTask(task);
        comment.setAuthor(author);

        // Логируем данные перед сохранением
        System.out.println("Saving comment with content: " + comment.getContent());

        commentRepository.save(comment);

        // Логируем данные после сохранения
        List<Comment> comments = commentRepository.findByTask(task);
        System.out.println("Comments found: " + comments.size());

        assertFalse(comments.isEmpty());
        assertEquals("Test Comment", comments.get(0).getContent());
    }

}
