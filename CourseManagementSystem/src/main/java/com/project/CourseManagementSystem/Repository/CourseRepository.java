package com.project.CourseManagementSystem.Repository;

import com.project.CourseManagementSystem.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.students WHERE c.id = :id")
    Course findByIdWithStudents(@Param("id") int id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.students")
    List<Course> findAllWithStudents();

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findCoursesByStudentId(@Param("studentId") int studentId);

}
