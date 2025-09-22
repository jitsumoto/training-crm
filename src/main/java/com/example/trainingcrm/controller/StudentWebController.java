package com.example.trainingcrm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.trainingcrm.service.StudentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentWebController {

    private final StudentService studentService;

    // 一覧画面
    @GetMapping("/students/list")
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students/list"; // → templates/students/list.html に対応
    }
}