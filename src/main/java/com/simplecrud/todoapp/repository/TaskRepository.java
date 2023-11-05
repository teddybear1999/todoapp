package com.simplecrud.todoapp.repository;

import com.simplecrud.todoapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByIsCompleted(boolean b);
}
