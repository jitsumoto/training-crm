package com.example.trainingcrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.trainingcrm.entity.Record;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
}