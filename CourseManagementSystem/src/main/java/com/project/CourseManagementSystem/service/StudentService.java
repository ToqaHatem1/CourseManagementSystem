package com.project.CourseManagementSystem.service;

import com.project.CourseManagementSystem.DTOs.StudentDTOs.StudentGetDTO;
import com.project.CourseManagementSystem.DTOs.StudentDTOs.StudentPostDTO;
import com.project.CourseManagementSystem.Repository.RoleRepository;
import com.project.CourseManagementSystem.Repository.StudentRepository;
import com.project.CourseManagementSystem.Repository.UserRepo;
import com.project.CourseManagementSystem.model.Role;
import com.project.CourseManagementSystem.model.Student;
import com.project.CourseManagementSystem.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private ModelMapper modelMapper;

    public Student findEntityById(int id) {
        return repo.findById(id).orElse(null);
    }

    public List<StudentGetDTO> getAllStudents(){
        return repo.findAll().stream()
                .map(student -> modelMapper.map(student, StudentGetDTO.class))
                .collect(Collectors.toList());
    }

    public StudentGetDTO getStudentById(int id){
        Student student = repo.findById(id).orElse(null);
        return (student != null) ? modelMapper.map(student, StudentGetDTO.class) : null;    }

    public StudentGetDTO addStudent(StudentPostDTO dto){
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

        assignStudentRole(user);

        Student student = new Student();
        student.setFullName(dto.getFullName());
        student.setEmail(dto.getEmail());
        student.setUser(user);

        Student saved = repo.save(student);

        return modelMapper.map(saved,StudentGetDTO.class);
//        StudentGetDTO result = modelMapper.map(saved, StudentGetDTO.class);
//        result.setUsername(saved.getUser().getUsername());
//        return result;
    }

    public StudentGetDTO updateStudent(int id, StudentPostDTO dto){
        Student existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));

        existing.setFullName(dto.getFullName());
        existing.setEmail(dto.getEmail());

        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

        assignStudentRole(user);
        existing.setUser(user);

        Student updated = repo.save(existing);

        return modelMapper.map(updated, StudentGetDTO.class);
//        StudentGetDTO result = modelMapper.map(updated, StudentGetDTO.class);
//        result.setUsername(updated.getUser().getUsername());
//        return result;
    }

    public void deleteStudent(int id){
        repo.deleteById(id);
    }

    //Helper Method to assign STUDENT Role
    private void assignStudentRole(User user) {
        boolean hasStudentRole = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("STUDENT"));
        if (!hasStudentRole) {
            Role studentRole = roleRepo.findByName("STUDENT")
                    .orElseThrow(() -> new RuntimeException("Role 'STUDENT' not found"));
            user.getRoles().add(studentRole);
            userRepo.save(user);  // Save role assignment
        }
    }
}
