package com.simplecrud.todoapp.repository;

import com.simplecrud.todoapp.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private Task completedTask;
    private Task uncompletedTask;

    @BeforeEach
    void setUp() {
        completedTask = new Task("Completed task", true, new Date());
        uncompletedTask = new Task("Uncompleted task", false, new Date());
        
        // Persist tasks using EntityManager for test setup
        entityManager.persistAndFlush(completedTask);
        entityManager.persistAndFlush(uncompletedTask);
    }

    @Test
    void findByIsCompleted_WithTrueValue_ReturnsOnlyCompletedTasks() {
        // Act
        List<Task> completedTasks = taskRepository.findByIsCompleted(true);

        // Assert
        assertEquals(1, completedTasks.size());
        assertTrue(completedTasks.get(0).getIsCompleted());
        assertEquals("Completed task", completedTasks.get(0).getDescription());
    }

    @Test
    void findByIsCompleted_WithFalseValue_ReturnsOnlyUncompletedTasks() {
        // Act
        List<Task> uncompletedTasks = taskRepository.findByIsCompleted(false);

        // Assert
        assertEquals(1, uncompletedTasks.size());
        assertFalse(uncompletedTasks.get(0).getIsCompleted());
        assertEquals("Uncompleted task", uncompletedTasks.get(0).getDescription());
    }

    @Test
    void findByIsCompleted_WithMultipleTasksOfSameStatus_ReturnsAllMatchingTasks() {
        // Arrange - Add more tasks with same completion status
        Task anotherCompletedTask = new Task("Another completed task", true, new Date());
        Task anotherUncompletedTask = new Task("Another uncompleted task", false, new Date());
        
        entityManager.persistAndFlush(anotherCompletedTask);
        entityManager.persistAndFlush(anotherUncompletedTask);

        // Act
        List<Task> completedTasks = taskRepository.findByIsCompleted(true);
        List<Task> uncompletedTasks = taskRepository.findByIsCompleted(false);

        // Assert
        assertEquals(2, completedTasks.size());
        assertEquals(2, uncompletedTasks.size());
        
        // Verify all completed tasks have isCompleted = true
        assertTrue(completedTasks.stream().allMatch(Task::getIsCompleted));
        
        // Verify all uncompleted tasks have isCompleted = false
        assertTrue(uncompletedTasks.stream().noneMatch(Task::getIsCompleted));
    }

    @Test
    void findByIsCompleted_WithNoMatchingTasks_ReturnsEmptyList() {
        // Arrange - Clear all tasks and add only completed tasks
        entityManager.clear();
        taskRepository.deleteAll();
        
        Task onlyCompletedTask = new Task("Only completed", true, new Date());
        entityManager.persistAndFlush(onlyCompletedTask);

        // Act
        List<Task> uncompletedTasks = taskRepository.findByIsCompleted(false);

        // Assert
        assertTrue(uncompletedTasks.isEmpty());
    }

    @Test
    void save_NewTask_PersistsTaskWithGeneratedId() {
        // Arrange
        Task newTask = new Task("New repository test task", false, new Date());

        // Act
        Task savedTask = taskRepository.save(newTask);

        // Assert
        assertNotNull(savedTask.getId());
        assertEquals("New repository test task", savedTask.getDescription());
        assertFalse(savedTask.getIsCompleted());
        assertNotNull(savedTask.getCreatedAt());
        assertNotNull(savedTask.getUpdatedAt());
    }

    @Test
    void save_ExistingTask_UpdatesTaskInDatabase() {
        // Arrange
        Task existingTask = entityManager.find(Task.class, completedTask.getId());
        existingTask.setDescription("Updated description");
        existingTask.setIsCompleted(false);

        // Act
        Task updatedTask = taskRepository.save(existingTask);

        // Assert
        assertEquals(completedTask.getId(), updatedTask.getId());
        assertEquals("Updated description", updatedTask.getDescription());
        assertFalse(updatedTask.getIsCompleted());
        assertNotNull(updatedTask.getUpdatedAt());
    }

    @Test
    void findById_ExistingTask_ReturnsTask() {
        // Act
        Optional<Task> foundTask = taskRepository.findById(completedTask.getId());

        // Assert
        assertTrue(foundTask.isPresent());
        assertEquals(completedTask.getId(), foundTask.get().getId());
        assertEquals("Completed task", foundTask.get().getDescription());
    }

    @Test
    void findById_NonExistingTask_ReturnsEmpty() {
        // Act
        Optional<Task> foundTask = taskRepository.findById(999L);

        // Assert
        assertFalse(foundTask.isPresent());
    }

    @Test
    void deleteById_ExistingTask_RemovesTaskFromDatabase() {
        // Arrange
        Long taskId = completedTask.getId();
        assertTrue(taskRepository.existsById(taskId));

        // Act
        taskRepository.deleteById(taskId);

        // Assert
        assertFalse(taskRepository.existsById(taskId));
        assertEquals(1, taskRepository.count()); // Only uncompletedTask should remain
    }

    @Test
    void count_WithMultipleTasks_ReturnsCorrectCount() {
        // Arrange - We have 2 tasks from setUp
        long initialCount = taskRepository.count();
        
        // Add one more task
        Task additionalTask = new Task("Additional task", true, new Date());
        taskRepository.save(additionalTask);

        // Act
        long newCount = taskRepository.count();

        // Assert
        assertEquals(2, initialCount);
        assertEquals(3, newCount);
    }
}