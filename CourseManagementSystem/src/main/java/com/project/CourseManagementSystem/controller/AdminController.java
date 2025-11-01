package com.project.CourseManagementSystem.controller;

import com.project.CourseManagementSystem.DTOs.UserRoleDTO;
import com.project.CourseManagementSystem.Repository.RoleRepository;
import com.project.CourseManagementSystem.Repository.UserRepo;
import com.project.CourseManagementSystem.model.Role;
import com.project.CourseManagementSystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/assign-role")
    public ResponseEntity<String> assignRole(@RequestBody UserRoleDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(dto.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);
        userRepo.save(user);

        return ResponseEntity.ok("Role '" + dto.getRoleName() + "' assigned to user: " + user.getUsername());
    }

    @DeleteMapping("/delete-role")
    public ResponseEntity<String> deleteRole(@RequestBody UserRoleDTO dto){
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(()-> new RuntimeException("User not Found"));

        Role role = roleRepository.findByName(dto.getRoleName())
                .orElseThrow(()-> new RuntimeException("Role Not Found"));

        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userRepo.save(user);
            return ResponseEntity.ok("Role '" + dto.getRoleName() + "' removed from user: " + user.getUsername());
        } else {
            return ResponseEntity.badRequest().body("User does not have the role: " + dto.getRoleName());
        }
    }

    @PutMapping("/update-role")
    public ResponseEntity<String> updateRole(@RequestBody UserRoleDTO dto){
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(()-> new RuntimeException("User not Found"));

        Role oldRole = roleRepository.findByName(dto.getRoleName())
                .orElseThrow(()-> new RuntimeException("Old Role Not Found"));

        Role newRole = roleRepository.findByName(dto.getNewRoleName())
                .orElseThrow(()-> new RuntimeException("New Role Not Found"));

        if (user.getRoles().contains(oldRole)) {
            user.getRoles().remove(oldRole);
            user.getRoles().add(newRole);
            userRepo.save(user);
            return ResponseEntity.ok("Role updated from '" + dto.getRoleName() + "' to '" + dto.getNewRoleName() + "' for user: " + user.getUsername());
        } else {
            return ResponseEntity.badRequest().body("User does not have the old role: " + dto.getRoleName());
        }
    }
}
