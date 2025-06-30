package com.simplecrud.todoapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecrud.todoapp.model.Task;
import com.simplecrud.todoapp.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class TaskApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/tasks";
        taskRepository.deleteAll();
    }

    @Test
    void createTask_ValidRequest_CreatesTaskInDatabase() {
        // Arrange
        Task newTask = new Task("Integration test task", false, new Date());

        // Act
        ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, newTask, Task.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Integration test task", response.getBody().getDescription());
        assertFalse(response.getBody().getIsCompleted());

        // Verify task was saved to database
        assertEquals(1, taskRepository.count());
    }

    @Test
    void getTaskById_ExistingTask_ReturnsTaskFromDatabase() {
        // Arrange - Save task to database
        Task savedTask = taskRepository.save(new Task("Database task", false, new Date()));

        // Act
        ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl + "/" + savedTask.getId(), Task.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(savedTask.getId(), response.getBody().getId());
        assertEquals("Database task", response.getBody().getDescription());
    }

    @Test
    void getTaskById_NonExistingTask_ReturnsNotFound() {
        // Act
        ResponseEntity<Task> response = restTemplate.getForEntity(baseUrl + "/999", Task.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getCompletedTasks_WithMixedTasks_ReturnsOnlyCompletedTasks() {
        // Arrange - Save mixed tasks to database
        taskRepository.save(new Task("Completed task 1", true, new Date()));
        taskRepository.save(new Task("Uncompleted task", false, new Date()));
        taskRepository.save(new Task("Completed task 2", true, new Date()));

        // Act
        ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl + "/completed", Task[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
        
        for (Task task : response.getBody()) {
            assertTrue(task.getIsCompleted());
        }
    }

    @Test
    void deleteTask_ExistingTask_RemovesTaskFromDatabase() {
        // Arrange - Save task to database
        Task savedTask = taskRepository.save(new Task("Task to delete", false, new Date()));
        Long taskId = savedTask.getId();

        // Act
        restTemplate.delete(baseUrl + "/" + taskId);

        // Assert - Verify task was removed from database
        assertFalse(taskRepository.existsById(taskId));
    }

    @Test
    void fullTaskLifecycle_CreateUpdateDelete_WorksEndToEnd() {
        // Create task
        Task newTask = new Task("Lifecycle test task", false, new Date());
        ResponseEntity<Task> createResponse = restTemplate.postForEntity(baseUrl, newTask, Task.class);
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Task createdTask = createResponse.getBody();
        assertNotNull(createdTask);
        Long taskId = createdTask.getId();

        // Update task completion
        restTemplate.put(baseUrl + "/" + taskId + "/completion?isCompleted=true", null);

        // Verify task was updated
        Optional<Task> updatedTask = taskRepository.findById(taskId);
        assertTrue(updatedTask.isPresent());
        assertTrue(updatedTask.get().getIsCompleted());

        // Delete task
        restTemplate.delete(baseUrl + "/" + taskId);

        // Verify task is deleted
        assertFalse(taskRepository.existsById(taskId));
    }
}