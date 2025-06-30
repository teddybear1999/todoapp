package com.simplecrud.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplecrud.todoapp.exceptions.CustomChineseException;
import com.simplecrud.todoapp.exceptions.自定义中文异常;
import com.simplecrud.todoapp.model.Task;
import com.simplecrud.todoapp.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getTask_ExistingTask_ReturnsTask() throws Exception {
        // Arrange
        when(taskService.findTaskById(1L)).thenReturn(Optional.of(sampleTask));

        // Act & Assert
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test task")))
                .andExpect(jsonPath("$.isCompleted", is(false)));

        verify(taskService).findTaskById(1L);
    }

    @Test
    void getTask_NonExistingTask_ReturnsNotFound() throws Exception {
        // Arrange
        when(taskService.findTaskById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound());

        verify(taskService).findTaskById(999L);
    }

    @Test
    void getCompletedTasks_ReturnsCompletedTasksList() throws Exception {
        // Arrange
        Task completedTask1 = new Task("Completed 1", true, new Date());
        completedTask1.setId(1L);
        Task completedTask2 = new Task("Completed 2", true, new Date());
        completedTask2.setId(2L);
        List<Task> completedTasks = Arrays.asList(completedTask1, completedTask2);

        when(taskService.findAllCompletedTasks()).thenReturn(completedTasks);

        // Act & Assert
        mockMvc.perform(get("/tasks/completed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].isCompleted", is(true)))
                .andExpect(jsonPath("$[1].isCompleted", is(true)));

        verify(taskService).findAllCompletedTasks();
    }

    @Test
    void getUncompletedTasks_ReturnsUncompletedTasksList() throws Exception {
        // Arrange
        Task uncompletedTask1 = new Task("Uncompleted 1", false, new Date());
        uncompletedTask1.setId(1L);
        Task uncompletedTask2 = new Task("Uncompleted 2", false, new Date());
        uncompletedTask2.setId(2L);
        List<Task> uncompletedTasks = Arrays.asList(uncompletedTask1, uncompletedTask2);

        when(taskService.findAllUncompletedTasks()).thenReturn(uncompletedTasks);

        // Act & Assert
        mockMvc.perform(get("/tasks/uncompleted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].isCompleted", is(false)))
                .andExpect(jsonPath("$[1].isCompleted", is(false)));

        verify(taskService).findAllUncompletedTasks();
    }

    @Test
    void getRandomBoolean_ReturnsRandomBooleanResponse() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/tasks/randomBoolean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", anyOf(is(true), is(false))));
    }

    @Test
    void createTask_ValidTask_ReturnsCreatedTask() throws Exception {
        // Arrange
        Task newTask = new Task("New task", false, new Date());
        Task createdTask = new Task("New task", false, new Date());
        createdTask.setId(1L);

        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("New task")))
                .andExpect(jsonPath("$.isCompleted", is(false)));

        verify(taskService).createTask(any(Task.class));
    }

    @Test
    void updateTaskCompletion_ExistingTask_ReturnsUpdatedTask() throws Exception {
        // Arrange
        Task updatedTask = new Task("Test task", true, new Date());
        updatedTask.setId(1L);

        when(taskService.updateTaskCompletionStatus(1L, true)).thenReturn(updatedTask);

        // Act & Assert
        mockMvc.perform(put("/tasks/1/completion")
                .param("isCompleted", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.isCompleted", is(true)));

        verify(taskService).updateTaskCompletionStatus(1L, true);
    }

    @Test
    void updateTaskCompletion_NonExistingTask_ReturnsNotFound() throws Exception {
        // Arrange
        when(taskService.updateTaskCompletionStatus(anyLong(), anyBoolean()))
                .thenThrow(new RuntimeException("Task not found"));

        // Act & Assert
        mockMvc.perform(put("/tasks/999/completion")
                .param("isCompleted", "true"))
                .andExpect(status().isNotFound());

        verify(taskService).updateTaskCompletionStatus(999L, true);
    }

    @Test
    void updateTaskDescription_ExistingTask_ReturnsUpdatedTask() throws Exception {
        // Arrange
        Task updatedTask = new Task("Updated description", false, new Date());
        updatedTask.setId(1L);

        when(taskService.updateTaskDescription(1L, "Updated description")).thenReturn(updatedTask);

        // Act & Assert
        mockMvc.perform(put("/tasks/1/description")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"Updated description\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Updated description")));

        verify(taskService).updateTaskDescription(1L, "Updated description");
    }

    @Test
    void updateTaskDescription_NonExistingTask_ReturnsNotFound() throws Exception {
        // Arrange
        when(taskService.updateTaskDescription(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Task not found"));

        // Act & Assert
        mockMvc.perform(put("/tasks/999/description")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"New description\""))
                .andExpect(status().isNotFound());

        verify(taskService).updateTaskDescription(999L, "New description");
    }

    @Test
    void deleteTask_CallsTaskService() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk());

        verify(taskService).deleteTask(1L);
    }

    @Test
    void deleteError_AlwaysReturnsInternalServerError() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/tasks/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("This DELETE endpoint always returns an error."));

        // Verify no interaction with taskService for this error endpoint
        verifyNoInteractions(taskService);
    }
}