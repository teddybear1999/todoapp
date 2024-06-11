package com.simplecrud.todoapp.controller;

import com.simplecrud.todoapp.model.Task;
import com.simplecrud.todoapp.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Get a task by ID
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        return taskService.findTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/completed")
    @ResponseBody
    public List<Task> getCompletedTasks() {
        return taskService.findAllCompletedTasks();
    }

    @GetMapping("/uncompleted")
    @ResponseBody
    public List<Task> getUncompletedTasks() {
        return taskService.findAllUncompletedTasks();
    }

    // Add new endpoint to return true or false randomly
    @GetMapping("/randomBoolean")
    @ResponseBody
    public Map<String, Boolean> getRandomBoolean() {
        Random random = new Random();
        boolean randomBoolean = random.nextBoolean();
        Map<String, Boolean> response = new HashMap<>();
        response.put("result", randomBoolean);
        return response;
    }

    // Create a new task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task newTask) {
        Task createdTask = taskService.createTask(newTask);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    // Update a task's completion status
    @PutMapping("/{id}/completion")
    @ResponseBody
    public ResponseEntity<Task> updateTaskCompletion(@PathVariable Long id, @RequestParam("isCompleted") boolean isCompleted) {
        try {
            Task updatedTask = taskService.updateTaskCompletionStatus(id, isCompleted);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update a task's description
    @PutMapping("/{id}/description")
    @ResponseBody
    public ResponseEntity<Task> updateTaskDescription(@PathVariable Long id, @RequestBody String description) {
        try {
            Task updatedTask = taskService.updateTaskDescription(id, description);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
