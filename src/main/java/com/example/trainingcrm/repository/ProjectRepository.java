package com.example.trainingcrm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trainingcrm.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatusOrderByStartDateAsc(String status);
}
