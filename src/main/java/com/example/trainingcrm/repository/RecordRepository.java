package com.example.trainingcrm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trainingcrm.entity.Record;

public interface RecordRepository extends JpaRepository<Record, Long> {
    // 学生ごとの成績一覧を取得
    List<Record> findByStudentStudentId(Long studentId);
}