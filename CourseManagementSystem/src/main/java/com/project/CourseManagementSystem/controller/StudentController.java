package com.project.CourseManagementSystem.controller;

import com.project.CourseManagementSystem.DTOs.CourseDTO;
import com.project.CourseManagementSystem.DTOs.StudentDTOs.StudentGetDTO;
import com.project.CourseManagementSystem.DTOs.StudentDTOs.StudentPostDTO;
import com.project.CourseManagementSystem.Repository.CourseRepository;
import com.project.CourseManagementSystem.model.Course;
import com.project.CourseManagementSystem.model.Student;
import com.project.CourseManagementSystem.service.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService service;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModelMapper modelMapper;

    //Get all students
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<StudentGetDTO>> getAllStudents(){
        return new ResponseEntity<>(service.getAllStudents(), HttpStatus.OK);
    }

    // Get a single student by id
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable int id, Authentication auth) {
        StudentGetDTO student = service.getStudentById(id);
        if(student == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        String username = auth.getName();
        boolean isSelf = student.getUsername().equals(username);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a-> a.getAuthority().equals("ADMIN"));
        if (!isSelf && !isAdmin){

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only view your own profile");

        }
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    // Add a new student
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<StudentGetDTO> addStudent(@RequestBody StudentPostDTO dto) {
        StudentGetDTO savedStudent = service.addStudent(dto);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    //Update an existing student
    @PreAuthorize("hasAnyAuthority('STUDENT', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable int id, @RequestBody StudentPostDTO dto, Authentication auth) {

        StudentGetDTO existingStudent = service.getStudentById(id);
        if (existingStudent == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        String username= auth.getName();

        boolean isSelf = existingStudent.getUsername().equals(username);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        // If not the owner or admin, deny access
        if (!isSelf && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own info");
        }

        StudentGetDTO updatedStudent = service.updateStudent(id, dto);

        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
    }

    //Delete a student
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable int id) {
        service.deleteStudent(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('ADMIN')")
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> getCoursesByStudent(@PathVariable int id, Authentication auth){
        Student student = service.findEntityById(id);

        if (student == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        String username = auth.getName();
        boolean isSelf = student.getUser().getUsername().equals(username);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!isSelf && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You can only view your own courses");
        }

        List<Course> courses = courseRepository.findCoursesByStudentId(id);

        // Map to DTOs using ModelMapper
        List<CourseDTO> courseDTOs = courses.stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .toList();

        return ResponseEntity.ok(courseDTOs);
    }
}
