package com.project.CourseManagementSystem.DTOs.InstructorDTOs;

import lombok.Data;

@Data
public class InstructorGetDTO {
    private int id;
    private String fullName;
    private String email;
    private String username;
    private String departmentName;
}
