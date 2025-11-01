package com.project.CourseManagementSystem.controller;

import com.project.CourseManagementSystem.model.Course;
import com.project.CourseManagementSystem.model.Department;
import com.project.CourseManagementSystem.model.Instructor;
import com.project.CourseManagementSystem.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService service;

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments(){
        return new ResponseEntity<>(service.getAllDepartments(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById (@PathVariable int id){
        Department department = service.getDepartmentById(id);
        if (department == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<Department> AddDepartment(@RequestBody Department department){
        Department savedDepartment = service.AddDepartment(department);
        return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Department> UpdateDepartment(@PathVariable int id, @RequestBody Department department){
        Department exisitingDepartment = service.UpdateDepartment(id, department);
        if (exisitingDepartment == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable int id){
        service.DeleteDepartment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/instructors")
    public ResponseEntity<List<Instructor>> getInstructorsByDepartment(@PathVariable int id){
        Department department = service.getDepartmentById(id);
        return ResponseEntity.ok(department.getInstructors());
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<List<Course>> getCoursesByDepartment(@PathVariable int id){
        Department department = service.getDepartmentById(id);

        // Collect all courses from instructors in that department
        List<Course> courses = department.getInstructors()
                .stream()
                .flatMap(i -> i.getCourses().stream())
                .toList();
        return ResponseEntity.ok(courses);
    }
}
