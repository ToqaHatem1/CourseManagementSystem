package com.project.CourseManagementSystem.service;

import com.project.CourseManagementSystem.DTOs.CourseDTO;
import com.project.CourseManagementSystem.DTOs.InstructorDTOs.InstructorGetDTO;
import com.project.CourseManagementSystem.DTOs.InstructorDTOs.InstructorPostDTO;
import com.project.CourseManagementSystem.Repository.DepartmentRepository;
import com.project.CourseManagementSystem.Repository.InstructorRepository;
import com.project.CourseManagementSystem.Repository.RoleRepository;
import com.project.CourseManagementSystem.Repository.UserRepo;
import com.project.CourseManagementSystem.model.Department;
import com.project.CourseManagementSystem.model.Instructor;
import com.project.CourseManagementSystem.model.Role;
import com.project.CourseManagementSystem.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepository repo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DepartmentRepository deptRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private ModelMapper modelMapper;

    public List<InstructorGetDTO> getAllInstructors(){
        return repo.findAll().stream()
                .map(i -> {
                    InstructorGetDTO dto = modelMapper.map(i, InstructorGetDTO.class);
                    dto.setUsername(i.getUser().getUsername());
                    dto.setDepartmentName(i.getDepartment().getName());
                    return dto;
                }).collect(Collectors.toList());
    }

    public InstructorGetDTO getInstructorById(int id){
        Instructor instructor = repo.findById(id).orElse(null);
        if (instructor == null) return null;

        InstructorGetDTO dto = modelMapper.map(instructor, InstructorGetDTO.class);
        dto.setUsername(instructor.getUser().getUsername());
        dto.setDepartmentName(instructor.getDepartment().getName());
        return dto;
    }

    public InstructorGetDTO addInstructor(InstructorPostDTO dto) {
        Instructor instructor = new Instructor();
        instructor.setFullName(dto.getFullName());
        instructor.setEmail(dto.getEmail());

        User user = userRepo.findById(dto.getUserId()).orElseThrow();
        assignInstructorRole(user);  // Automatically assign role
        Department department = deptRepo.findById(dto.getDepartmentId()).orElseThrow();
        instructor.setUser(user);
        instructor.setDepartment(department);

        Instructor saved = repo.save(instructor);

        InstructorGetDTO result = modelMapper.map(saved, InstructorGetDTO.class);
        result.setUsername(saved.getUser().getUsername());
        result.setDepartmentName(saved.getDepartment().getName());
        return result;
    }

    public InstructorGetDTO updateInstructor(int id, InstructorPostDTO dto) {
        Instructor existing = repo.findById(id).orElse(null);
        if (existing == null) return null;

        existing.setFullName(dto.getFullName());
        existing.setEmail(dto.getEmail());

        User user = userRepo.findById(dto.getUserId()).orElseThrow();
        assignInstructorRole(user);  // Automatically assign role
        existing.setUser(user);

        Department department = deptRepo.findById(dto.getDepartmentId()).orElseThrow();
        existing.setDepartment(department);

        Instructor saved = repo.save(existing);

        InstructorGetDTO result = modelMapper.map(saved, InstructorGetDTO.class);
        result.setUsername(saved.getUser().getUsername());
        result.setDepartmentName(saved.getDepartment().getName());
        return result;
    }

    public void deleteInstructor(int id) {
        repo.deleteById(id);
    }

    public List<CourseDTO> getCoursesByInstructorCourses(int id) {
        Instructor instructor = repo.findById(id).orElseThrow(() -> new RuntimeException("Instructor not found"));

        return instructor.getCourses().stream()
                .map(c -> {
                    CourseDTO dto = new CourseDTO();
                    dto.setId(c.getId());
                    dto.setName(c.getName());
                    dto.setCreditHours(c.getCreditHours());
                    return dto;
                }).collect(Collectors.toList());
    }

    //Helper Method to assign INSTRUCTOR Role
    private void assignInstructorRole(User user) {
        boolean hasInstructorRole = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("INSTRUCTOR"));
        if (!hasInstructorRole) {
            Role instructorRole = roleRepo.findByName("INSTRUCTOR")
                    .orElseThrow(() -> new RuntimeException("Role 'INSTRUCTOR' not found"));
            user.getRoles().add(instructorRole);
            userRepo.save(user);  // Save role assignment
        }
    }


}
