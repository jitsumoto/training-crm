package com.example.trainingcrm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.trainingcrm.entity.Student;
import com.example.trainingcrm.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    // 全件取得
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // IDで取得
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // 登録
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    // 更新
    public Student updateStudent(Long id, Student studentDetails) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.setName(studentDetails.getName());
                    student.setEmail(studentDetails.getEmail());
                    student.setPhone(studentDetails.getPhone());
                    student.setClassName(studentDetails.getClassName());
                    return studentRepository.save(student);
                })
                .orElseThrow(() -> new RuntimeException("Student not found with id " + id));
    }

    // 削除
    public void deleteStudent(Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Student not found with id " + id);
        }
    }
}