package com.example.trainingcrm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trainingcrm.entity.Activity;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByTaskTaskIdOrderByActivityAtDesc(Long taskId);
}
