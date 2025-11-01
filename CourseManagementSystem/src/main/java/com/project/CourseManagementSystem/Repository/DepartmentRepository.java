package com.project.CourseManagementSystem.Repository;

import com.project.CourseManagementSystem.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}
