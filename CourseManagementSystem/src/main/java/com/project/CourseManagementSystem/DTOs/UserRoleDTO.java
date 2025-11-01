package com.project.CourseManagementSystem.DTOs;

import lombok.Data;

@Data
public class UserRoleDTO {
    private int userId;
    private String roleName;
    private String newRoleName; // used only for update
}
