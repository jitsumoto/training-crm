package com.example.trainingcrm.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.trainingcrm.entity.Record;
import com.example.trainingcrm.entity.Student;
import com.example.trainingcrm.service.RecordService;
import com.example.trainingcrm.service.StudentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/students/{studentId}/records")
@RequiredArgsConstructor
public class RecordWebController {

    private final StudentService studentService;
    private final RecordService recordService;

    /**
     * 成績一覧表示
     */
    @GetMapping("/list")
    public String listRecords(@PathVariable Long studentId, Model model) {
        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        List<Record> records = recordService.getRecordsByStudent(studentId);

        model.addAttribute("student", student);
        model.addAttribute("records", records);
        return "records/list";  // → templates/records/list.html
    }

    /**
     * 新規成績フォーム
     */
    @GetMapping("/new")
    public String newRecordForm(@PathVariable Long studentId, Model model) {
        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        model.addAttribute("student", student);
        model.addAttribute("record", new Record());
        return "records/form"; // → templates/records/form.html
    }

    /**
     * 成績保存処理
     */
    @PostMapping("/save")
    public String saveRecord(@PathVariable Long studentId, @ModelAttribute Record record) {
        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        record.setStudent(student); // student を紐づけ
        recordService.createRecord(record);

        return "redirect:/students/" + studentId + "/records/list";
    }

    /**
     * 編集フォーム
     */
    @GetMapping("/edit/{recordId}")
    public String editRecord(@PathVariable Long studentId,
                             @PathVariable Long recordId,
                             Model model) {
        Student student = studentService.getStudentById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Record record = recordService.getRecord(recordId)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        model.addAttribute("student", student);
        model.addAttribute("record", record);
        return "records/form"; // 既存の form.html を使う
    }

    /**
     * 削除処理
     */
    @GetMapping("/delete/{recordId}")
    public String deleteRecord(@PathVariable Long studentId, @PathVariable Long recordId) {
        recordService.deleteRecord(recordId);
        return "redirect:/students/" + studentId + "/records/list";
    }
}