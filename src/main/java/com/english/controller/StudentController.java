package com.english.controller;

import com.english.model.Student;
import com.english.service.StudentService;

import javax.swing.*;
import java.util.List;

public class StudentController {
    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    public void insertStudent(Student student, JFrame frame) {
        if (studentService.insertStudent(student)) {
            JOptionPane.showMessageDialog(frame,
                    "Student has been inserted!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Please check the information",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateStudent(Student student, JFrame frame) {
        if (studentService.updateStudent(student)) {
            JOptionPane.showMessageDialog(frame,
                    "Student has been updated!",
                    "Successfully",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Updating student failed!",
                    "Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteStudent(Student student, JFrame frame) {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are your sure to delete this student?",
                "Deleting confirm",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (studentService.deleteStudent(student)) {
                JOptionPane.showMessageDialog(frame,
                        "Student has been deleted!",
                        "Successfully",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Deleting student failed!",
                        "Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    public Student getStudentById(String studentId) {
        return studentService.getStudentById(studentId);
    }

    public List<Student> getStudentByTargetBand (double targetBand) {
        return studentService.getStudentByTargetBand(targetBand);
    }

    public int totalStudents() {
        return studentService.totalStudents();
    }
}
