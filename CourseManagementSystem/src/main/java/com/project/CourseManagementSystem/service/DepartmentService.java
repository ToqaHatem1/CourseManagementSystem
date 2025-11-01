package com.project.CourseManagementSystem.service;

import com.project.CourseManagementSystem.Repository.DepartmentRepository;
import com.project.CourseManagementSystem.model.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository repo;

    public List<Department> getAllDepartments(){
        return repo.findAll();
    }

    public Department getDepartmentById(int id){
        return repo.findById(id).orElse(null);
    }

    public Department AddDepartment(Department department){
        return repo.save(department);
    }

    public Department UpdateDepartment(int id, Department department){
        Department existing = repo.findById(id).orElse(null);
        if(existing == null){
            return null;
        }
        existing.setName(department.getName());
        existing.setDescription(department.getDescription());
        existing.setInstructors(department.getInstructors());
        return repo.save(existing);
    }

    public void DeleteDepartment(int id){
        repo.deleteById(id);
    }


}
