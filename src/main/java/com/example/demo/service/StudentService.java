package com.example.demo.service;

import com.example.demo.entity.Student; 
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StudentService {

    private final String filePath = "students.txt";
    private List<Student> students = new ArrayList<>();
    private AtomicLong counter = new AtomicLong();

    @PostConstruct
    private void init() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filePath);
        if (file.exists()) {
            try {
                students = mapper.readValue(file, new TypeReference<List<Student>>() {});
                if (!students.isEmpty()) {
                    counter.set(students.stream().mapToLong(Student::getId).max().orElse(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveToFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(filePath), students);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Student> getAllStudents() {
        return students;
    }

    public Optional<Student> getStudentById(Long id) {
        return students.stream().filter(s -> s.getId().equals(id)).findFirst();
    }

    public Student createStudent(Student student) {
        student.setId(counter.incrementAndGet());
        students.add(student);
        saveToFile();
        return student;
    }

    public Student updateStudent(Long id, Student studentDetails) {
        Optional<Student> studentOpt = getStudentById(id);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setName(studentDetails.getName());
            student.setEmail(studentDetails.getEmail());
            student.setCourse(studentDetails.getCourse());
            student.setAge(studentDetails.getAge());
            saveToFile();
            return student;
        } else {
            throw new RuntimeException("Student not found");
        }
    }

    public void deleteStudent(Long id) {
        students.removeIf(s -> s.getId().equals(id));
        saveToFile();
    }
}
