package com.example.trainingcrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.trainingcrm.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}