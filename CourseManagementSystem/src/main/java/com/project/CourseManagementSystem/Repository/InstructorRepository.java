package com.project.CourseManagementSystem.Repository;

import com.project.CourseManagementSystem.model.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
}
