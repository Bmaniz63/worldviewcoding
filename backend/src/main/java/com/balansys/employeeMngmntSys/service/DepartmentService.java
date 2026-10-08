package com.balansys.employeeMngmntSys.service;

import com.balansys.employeeMngmntSys.model.dto.DepartmentResponse;
import com.balansys.employeeMngmntSys.model.entity.Department;
import com.balansys.employeeMngmntSys.model.exception.DepartmentNotFoundException;
import com.balansys.employeeMngmntSys.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentResponse> getAllDepartments() {

        return departmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Department getDepartmentById(Long departmentId) {

        return departmentRepository.findById(departmentId)
                .orElseThrow(() ->
                        new DepartmentNotFoundException(
                                departmentId
                        )
                );
    }

    private DepartmentResponse toResponse(
            Department department) {

        return new DepartmentResponse(
                department.getId(),
                department.getName()
        );
    }
}
