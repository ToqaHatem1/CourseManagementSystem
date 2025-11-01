package com.project.CourseManagementSystem.service;

import com.project.CourseManagementSystem.DTOs.CourseDTO;
import com.project.CourseManagementSystem.Repository.CourseRepository;
import com.project.CourseManagementSystem.model.Course;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired
    private CourseRepository repo;

    @Autowired
    private ModelMapper modelMapper;

    public List<CourseDTO> getAllCourses(){
        List<Course> courses = repo.findAllWithStudents();

        // Convert each Course to CourseDTO
        return courses.stream()
                .map(course -> modelMapper.map(course, CourseDTO.class))
                .collect(Collectors.toList());
    }

    public Course getCourseById(int id){
        return repo.findById(id).orElse(null);
    }

    public Course getCourseByIdWithStudents(int id){
        return repo.findByIdWithStudents(id);
    }

    public Course addCourse(Course course){
        return repo.save(course);
    }

    public Course updateCourse(int id, Course course) {
        Course existingCourse = repo.findById(id).orElse(null);
        if(existingCourse == null){
            return null;
        }
        existingCourse.setName(course.getName());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setCreditHours(course.getCreditHours());
        existingCourse.setInstructor(course.getInstructor());

        return repo.save(existingCourse);
    }

    public void deleteCourse(int id){
        repo.deleteById(id);
    }
}
