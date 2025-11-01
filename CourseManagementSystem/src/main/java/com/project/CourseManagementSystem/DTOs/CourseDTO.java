package com.project.CourseManagementSystem.DTOs;

import com.project.CourseManagementSystem.DTOs.StudentDTOs.StudentGetDTO;
import lombok.Data;

import java.util.List;

@Data
public class CourseDTO {
    private int id;
    private String name;
    private String description;
    private int creditHours;
    private List<StudentGetDTO> students;
}
