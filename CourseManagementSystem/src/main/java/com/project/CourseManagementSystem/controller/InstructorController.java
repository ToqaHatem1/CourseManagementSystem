package com.project.CourseManagementSystem.controller;

import com.project.CourseManagementSystem.DTOs.CourseDTO;
import com.project.CourseManagementSystem.DTOs.InstructorDTOs.InstructorGetDTO;
import com.project.CourseManagementSystem.DTOs.InstructorDTOs.InstructorPostDTO;
import com.project.CourseManagementSystem.model.Course;
import com.project.CourseManagementSystem.model.Instructor;
import com.project.CourseManagementSystem.service.InstructorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/instructors")
public class InstructorController {

    @Autowired
    private InstructorService service;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<InstructorGetDTO>> getAllInstructors(){
        List<InstructorGetDTO> instructors = service.getAllInstructors();
        return ResponseEntity.ok(instructors);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getInstructorById(@PathVariable int id, Authentication auth){
        InstructorGetDTO dto = service.getInstructorById(id);
        if (dto == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instructor not found");

        String username = auth.getName();
        boolean isSelf = dto.getUsername().equals(username);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        if (!isSelf && !isAdmin) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only view your own profile");

        return ResponseEntity.ok(dto);

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<InstructorGetDTO> AddInstructor(@RequestBody InstructorPostDTO dto){
        InstructorGetDTO saved = service.addInstructor(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('INSTRUCTOR', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> UpdateInstructor(@PathVariable int id, @RequestBody InstructorPostDTO dto, Authentication auth){
        InstructorGetDTO existing = service.getInstructorById(id);
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Instructor not found");

        String username = auth.getName();
        boolean isSelf = existing.getUsername().equals(username);
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        if (!isSelf && !isAdmin) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own profile");

        InstructorGetDTO updated = service.updateInstructor(id, dto);
        return ResponseEntity.ok(updated);

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> DeleteInstructor(@PathVariable int id){
        service.deleteInstructor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<CourseDTO>> getCoursesByInstructor(@PathVariable int id, Authentication auth) {
        InstructorGetDTO instructor = service.getInstructorById(id);
        if (instructor == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String username = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"));
        if (!isAdmin && !instructor.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<CourseDTO> courses = service.getCoursesByInstructorCourses(id);
        return ResponseEntity.ok(courses);
    }
}
