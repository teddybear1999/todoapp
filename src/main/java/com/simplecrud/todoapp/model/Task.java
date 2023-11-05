package com.simplecrud.todoapp.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @Column(name = "due_date")
    private Date dueDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    // Constructors, if necessary

    public Task() {
    }


    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // No setter for createdAt, as it's set automatically by the database

    public Date getUpdatedAt() {
        return updatedAt;
    }

    // No setter for updatedAt, as it's set automatically by the database

    public Task(String description, Boolean isCompleted, Date dueDate) {
        this.description = description;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
    }

    // toString, hashCode, equals, etc.

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", isCompleted=" + isCompleted +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return Objects.equals(getId(), task.getId()) && Objects.equals(getDescription(), task.getDescription()) &&
                Objects.equals(getIsCompleted(), task.getIsCompleted()) &&
                Objects.equals(getDueDate(), task.getDueDate()) && Objects.equals(getCreatedAt(), task.getCreatedAt())
                && Objects.equals(getUpdatedAt(), task.getUpdatedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDescription(), getIsCompleted(), getDueDate(), getCreatedAt(), getUpdatedAt());
    }
}