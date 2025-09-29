package com.example.trainingcrm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trainingcrm.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectProjectIdOrderByDueDateAsc(Long projectId);
}
