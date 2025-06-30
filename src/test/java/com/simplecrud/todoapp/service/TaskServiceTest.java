package com.simplecrud.todoapp.service;

import com.simplecrud.todoapp.model.Task;
import com.simplecrud.todoapp.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setDescription("Test task");
        sampleTask.setIsCompleted(false);
        sampleTask.setDueDate(new Date());
    }

    @Test
    void findTaskById_ExistingTask_ReturnsTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        // Act
        Optional<Task> result = taskService.findTaskById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(sampleTask.getId(), result.get().getId());
        assertEquals(sampleTask.getDescription(), result.get().getDescription());
        verify(taskRepository).findById(1L);
    }

    @Test
    void findTaskById_NonExistingTask_ReturnsEmpty() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<Task> result = taskService.findTaskById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(taskRepository).findById(999L);
    }

    @Test
    void findAllCompletedTasks_ReturnsCompletedTasks() {
        // Arrange
        Task completedTask1 = new Task("Completed task 1", true, new Date());
        Task completedTask2 = new Task("Completed task 2", true, new Date());
        List<Task> completedTasks = Arrays.asList(completedTask1, completedTask2);
        
        when(taskRepository.findByIsCompleted(true)).thenReturn(completedTasks);

        // Act
        List<Task> result = taskService.findAllCompletedTasks();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Task::getIsCompleted));
        verify(taskRepository).findByIsCompleted(true);
    }

    @Test
    void findAllUncompletedTasks_ReturnsUncompletedTasks() {
        // Arrange
        Task uncompletedTask1 = new Task("Uncompleted task 1", false, new Date());
        Task uncompletedTask2 = new Task("Uncompleted task 2", false, new Date());
        List<Task> uncompletedTasks = Arrays.asList(uncompletedTask1, uncompletedTask2);
        
        when(taskRepository.findByIsCompleted(false)).thenReturn(uncompletedTasks);

        // Act
        List<Task> result = taskService.findAllUncompletedTasks();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(Task::getIsCompleted));
        verify(taskRepository).findByIsCompleted(false);
    }

    @Test
    void createTask_ValidTask_CreatesAndReturnsTask() {
        // Arrange
        Task newTask = new Task("New task", null, new Date());
        newTask.setId(5L); // This should be nulled by the service
        
        Task savedTask = new Task("New task", false, new Date());
        savedTask.setId(10L);
        
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task result = taskService.createTask(newTask);

        // Assert
        assertNotNull(result);
        assertEquals(savedTask.getId(), result.getId());
        assertEquals(savedTask.getDescription(), result.getDescription());
        assertFalse(result.getIsCompleted());
        
        // Verify that ID was set to null and isCompleted was set to false
        verify(taskRepository).save(argThat(task -> 
            task.getId() == null && 
            task.getIsCompleted() == false &&
            task.getDescription().equals("New task")
        ));
    }

    @Test
    void updateTaskCompletionStatus_ExistingTask_UpdatesAndReturnsTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setDescription("Test task");
        updatedTask.setIsCompleted(true);
        
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        Task result = taskService.updateTaskCompletionStatus(1L, true);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsCompleted());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(sampleTask);
        assertTrue(sampleTask.getIsCompleted()); // Verify the original object was modified
    }

    @Test
    void updateTaskCompletionStatus_NonExistingTask_ThrowsRuntimeException() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> taskService.updateTaskCompletionStatus(999L, true));
        
        assertEquals("Task not found with id: 999", exception.getMessage());
        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTaskDescription_ExistingTask_UpdatesAndReturnsTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        
        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setDescription("Updated description");
        updatedTask.setIsCompleted(false);
        
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        // Act
        Task result = taskService.updateTaskDescription(1L, "Updated description");

        // Assert
        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(sampleTask);
        assertEquals("Updated description", sampleTask.getDescription()); // Verify the original object was modified
    }

    @Test
    void updateTaskDescription_NonExistingTask_ThrowsRuntimeException() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> taskService.updateTaskDescription(999L, "New description"));
        
        assertEquals("Task not found with id: 999", exception.getMessage());
        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTask_CallsRepositoryDeleteById() {
        // Arrange
        Long taskId = 1L;

        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(taskRepository).deleteById(taskId);
    }
}