package com.project.CourseManagementSystem.DTOs.InstructorDTOs;

import lombok.Data;

@Data
public class InstructorPostDTO {
    private String fullName;
    private String email;
    private int userId;
    private int departmentId;
}
