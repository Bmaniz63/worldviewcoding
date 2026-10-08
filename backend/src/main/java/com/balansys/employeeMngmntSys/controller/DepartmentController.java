package com.balansys.employeeMngmntSys.controller;

import com.balansys.employeeMngmntSys.model.dto.DepartmentResponse;
import com.balansys.employeeMngmntSys.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public List<DepartmentResponse> getDepartments() {

        return departmentService.getAllDepartments();
    }
}

