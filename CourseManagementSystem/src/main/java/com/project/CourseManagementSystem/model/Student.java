package com.project.CourseManagementSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Entity //Tells Spring Data JPA (Hibernate) that this class represents a table in your database.
@Data //It automatically generates boilerplate code you’d normally have to write manually, like: getters and setters, etc.
@NoArgsConstructor //It generates a no-arguments constructor (an empty constructor).
@AllArgsConstructor //It generates a constructor with all class fields as parameters.
@Builder //Generates a Builder pattern for your class — a convenient way to create objects with chained methods.
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fullName;
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();
}
