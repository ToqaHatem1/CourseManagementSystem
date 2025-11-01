package com.project.CourseManagementSystem.Repository;

import com.project.CourseManagementSystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {

    //This method might return a User, or it might return nothing — and we’re wrapping that in an Optional so you can handle both cases safely
    Optional<User> findByUsername (String username);
}
