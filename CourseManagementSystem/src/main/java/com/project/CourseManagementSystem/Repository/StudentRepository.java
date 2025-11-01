package com.project.CourseManagementSystem.Repository;

import com.project.CourseManagementSystem.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUserUsername(String username);
}
