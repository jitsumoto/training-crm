package com.example.trainingcrm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.trainingcrm.entity.Record;
import com.example.trainingcrm.repository.RecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;

    public List<Record> getRecordsByStudent(Long studentId) {
        return recordRepository.findByStudentStudentId(studentId);
    }

    public Optional<Record> getRecord(Long id) {
        return recordRepository.findById(id);
    }

    public Record createRecord(Record record) {
        return recordRepository.save(record);
    }

    public Record updateRecord(Long id, Record recordDetails) {
        return recordRepository.findById(id)
                .map(record -> {
                    record.setTestDate(recordDetails.getTestDate());
                    record.setLevel(recordDetails.getLevel());
                    record.setScore(recordDetails.getScore());
                    return recordRepository.save(record);
                })
                .orElseThrow(() -> new RuntimeException("Record not found with id " + id));
    }

    public void deleteRecord(Long id) {
        if (recordRepository.existsById(id)) {
            recordRepository.deleteById(id);
        } else {
            throw new RuntimeException("Record not found with id " + id);
        }
    }
}