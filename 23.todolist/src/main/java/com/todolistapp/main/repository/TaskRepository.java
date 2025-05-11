package com.todolistapp.main.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.todolistapp.main.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {}
