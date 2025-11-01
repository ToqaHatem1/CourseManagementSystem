package com.project.CourseManagementSystem.DTOs.StudentDTOs;

import com.project.CourseManagementSystem.DTOs.CourseDTO;
import lombok.Data;

import java.util.List;

@Data
public class StudentGetDTO {
    private int id;
    private String fullName;
    private String email;
    private String username;
    private List<CourseDTO> courses;
}
