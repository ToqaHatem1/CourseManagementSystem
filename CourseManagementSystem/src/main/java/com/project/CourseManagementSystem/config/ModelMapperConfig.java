package com.project.CourseManagementSystem.config;

import com.project.CourseManagementSystem.DTOs.CourseDTO;
import com.project.CourseManagementSystem.DTOs.InstructorDTOs.InstructorGetDTO;
import com.project.CourseManagementSystem.DTOs.StudentDTOs.StudentGetDTO;
import com.project.CourseManagementSystem.model.Course;
import com.project.CourseManagementSystem.model.Instructor;
import com.project.CourseManagementSystem.model.Student;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Map nested User.username -> InstructorGetDTO.username
        mapper.typeMap(Instructor.class, InstructorGetDTO.class)
                .addMapping(src -> src.getUser().getUsername(), InstructorGetDTO::setUsername)
                .addMapping(src -> src.getDepartment().getName(), InstructorGetDTO::setDepartmentName);

        mapper.typeMap(Student.class, StudentGetDTO.class)
                .addMapping(src -> src.getUser().getUsername(), StudentGetDTO::setUsername)
                .addMappings(m -> m.skip(StudentGetDTO::setCourses));

        mapper.typeMap(Course.class, CourseDTO.class)
                .addMappings(m -> m.skip(CourseDTO::setStudents)); // Skip to prevent deep circular mapping

        return mapper;
    }
}
