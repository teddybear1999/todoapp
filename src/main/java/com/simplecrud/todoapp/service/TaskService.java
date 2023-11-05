package com.simplecrud.todoapp.service;

import com.simplecrud.todoapp.model.Task;
import com.simplecrud.todoapp.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findAllCompletedTasks() {
        return taskRepository.findByIsCompleted(true);
    }

    public List<Task> findAllUncompletedTasks() {
        return taskRepository.findByIsCompleted(false);
    }

    public Task createTask(Task newTask) {
        newTask.setId(null);
        newTask.setIsCompleted(false);
        return taskRepository.save(newTask);
    }

    public Task updateTaskCompletionStatus(Long id, Boolean isCompleted) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setIsCompleted(isCompleted);
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found with id: " + id);
        }
    }

    public Task updateTaskDescription(Long id, String description) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setDescription(description);
            return taskRepository.save(task);
        } else {
            throw new RuntimeException("Task not found with id: " + id);
        }
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
