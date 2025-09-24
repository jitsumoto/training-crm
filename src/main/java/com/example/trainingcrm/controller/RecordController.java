package com.example.trainingcrm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trainingcrm.entity.Record;
import com.example.trainingcrm.entity.Student;
import com.example.trainingcrm.repository.StudentRepository;
import com.example.trainingcrm.service.RecordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/students/{studentId}/records") // ← 「特定の student の成績データ」を扱う API
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final StudentRepository studentRepository; // ← Student 情報を取得するために追加

    // 全レコード取得
    @GetMapping
    public List<Record> getAllRecords(@PathVariable Long studentId) {
        // studentId に紐づく records をサービスから取得
        return recordService.getRecordsByStudent(studentId);
    }

    // 特定のレコード取得
    @GetMapping("/{recordId}")
    public ResponseEntity<Record> getRecordById(@PathVariable Long recordId) {
        return recordService.getRecord(recordId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // レコード作成
    @PostMapping
    public ResponseEntity<Record> createRecord(
            @PathVariable Long studentId,  // ← URL の {studentId} を受け取る
            @RequestBody Record record) { // ← JSON の中身は testDate / level / score

        // ① studentId で Student を検索
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id " + studentId));

        // ② Record に Student を紐づける
        record.setStudent(student);

        // ③ 保存して返す
        Record savedRecord = recordService.createRecord(record);
        return ResponseEntity.ok(savedRecord);
    }

    // レコード更新
    @PutMapping("/{recordId}")
    public ResponseEntity<Record> updateRecord(
            @PathVariable Long recordId,
            @RequestBody Record recordDetails) {
        try {
            return ResponseEntity.ok(recordService.updateRecord(recordId, recordDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // レコード削除
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long recordId) {
        try {
            recordService.deleteRecord(recordId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}