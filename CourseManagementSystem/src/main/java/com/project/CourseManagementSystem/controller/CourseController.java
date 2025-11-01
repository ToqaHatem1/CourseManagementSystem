package com.project.CourseManagementSystem.controller;

import com.project.CourseManagementSystem.DTOs.CourseDTO;
import com.project.CourseManagementSystem.DTOs.StudentDTOs.StudentGetDTO;
import com.project.CourseManagementSystem.Repository.StudentRepository;
import com.project.CourseManagementSystem.model.Course;
import com.project.CourseManagementSystem.model.Student;
import com.project.CourseManagementSystem.service.CourseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService service;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StudentRepository studentRepo;

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses(){
        return new ResponseEntity<>(service.getAllCourses(),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable int id){
        Course course = service.getCourseById(id);
        if (course == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(course, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR', 'ADMIN')")
    @PostMapping
    public ResponseEntity<Course> addCourse(@RequestBody Course course){
        Course savedCourse = service.addCourse(course);
        return new ResponseEntity<>(savedCourse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable int id, @RequestBody Course course, Authentication auth){
        Course existingCourse = service.getCourseById(id);
        if (existingCourse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        String username = auth.getName();

        // Check if the logged-in user is the instructor who owns the course
        //check for null before accessing the instructor
        boolean isOwner = false;
        if (existingCourse.getInstructor() != null && existingCourse.getInstructor().getUser() != null) {
            isOwner = existingCourse.getInstructor().getUser().getUsername().equals(username);
        }
        // Check if the logged-in user is an admin
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        // If not the owner or admin, deny access
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own courses");
        }

        Course updatedCourse = service.updateCourse(id, course);

        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse (@PathVariable int id){
        service.deleteCourse(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{courseId}/enroll")
    public ResponseEntity<List<StudentGetDTO>> getStudentsByCourse(@PathVariable int courseId, Authentication auth){
        Course course = service.getCourseByIdWithStudents(courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        //Only the instructor who owns the course or admin can view students
        String username= auth.getName();

        boolean isOwner = false;
        if (course.getInstructor() != null && course.getInstructor().getUser() != null) {
            isOwner = course.getInstructor().getUser().getUsername().equals(username);
        }

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<StudentGetDTO> studentDTOs = course.getStudents().stream()
                .map(student -> modelMapper.map(student, StudentGetDTO.class))
                .toList();

        return ResponseEntity.ok(studentDTOs);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<String> enrollCourse(@PathVariable int courseId, Authentication auth){
        Course course = service.getCourseById(courseId);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }
        Student student = studentRepo.findByUserUsername(auth.getName())
                .orElseThrow(()-> new RuntimeException("Student Not Found"));

        if (course.getStudents().contains(student)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Already enrolled in this course");
        }

        student.getCourses().add(course);
        studentRepo.save(student);

        return ResponseEntity.ok("Enrolled successfully!");
    }

//    @PreAuthorize("hasAuthority('STUDENT')")
//    @DeleteMapping("/{courseId}/unenroll")
//    public ResponseEntity<String> unenrollCourse(@PathVariable int courseId, Authentication auth){
//        Course course = service.getCourseById(courseId);
//        if (course == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
//        }
//        Student student = studentRepo.findByUserUsername(auth.getName())
//                .orElseThrow(()-> new RuntimeException("Student Not Found"));
//
//        // Check if the student is enrolled in the course
//        Set<Course> coursesCopy = new HashSet<>(student.getCourses());
//        if (!coursesCopy.contains(course)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not enrolled in this course");
//        }
//        student.getCourses().remove(course);
//        course.getStudents().remove(student);
//
//        studentRepo.save(student);
//        service.addCourse(course);
//
//        return ResponseEntity.ok("Unenrolled successfully!");
//    }
}
