package com.balansys.employeeMngmntSys.service;

import com.balansys.employeeMngmntSys.model.dto.DepartmentResponse;
import com.balansys.employeeMngmntSys.model.dto.EmployeeRequest;
import com.balansys.employeeMngmntSys.model.dto.EmployeeResponse;
import com.balansys.employeeMngmntSys.model.entity.Department;
import com.balansys.employeeMngmntSys.model.entity.Employee;
import com.balansys.employeeMngmntSys.model.exception.DuplicateEmailException;
import com.balansys.employeeMngmntSys.model.exception.EmployeeNotFoundException;
import com.balansys.employeeMngmntSys.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {

        return employeeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {

        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(() ->
                                new EmployeeNotFoundException(id));

        return toResponse(employee);
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {

        String email = normalizeEmail(request.getEmail());

        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(email);
        }

        Department department =
                departmentService.getDepartmentById(
                        request.getDepartmentId());

        Employee employee = new Employee(
                request.getFirstName(),
                request.getLastName(),
                email,
                department
        );

        Employee savedEmployee =
                employeeRepository.save(employee);

        return toResponse(savedEmployee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(
            Long id,
            EmployeeRequest request) {

        Employee existingEmployee =
                employeeRepository.findById(id)
                        .orElseThrow(() ->
                                new EmployeeNotFoundException(id));

        String email = normalizeEmail(request.getEmail());

        if (employeeRepository
                .existsByEmailIgnoreCaseAndIdNot(email, id)) {

            throw new DuplicateEmailException(email);
        }

        Department department =
                departmentService.getDepartmentById(
                        request.getDepartmentId());

        existingEmployee.setFirstName(
                request.getFirstName());

        existingEmployee.setLastName(
                request.getLastName());

        existingEmployee.setEmail(email);

        existingEmployee.setDepartment(department);

        Employee updatedEmployee =
                employeeRepository.save(existingEmployee);

        return toResponse(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {

        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }

        employeeRepository.deleteById(id);
    }

    private String normalizeEmail(String email) {

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private EmployeeResponse toResponse(Employee employee) {

        Department department =
                employee.getDepartment();

        DepartmentResponse departmentResponse =
                new DepartmentResponse(
                        department.getId(),
                        department.getName()
                );

        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                departmentResponse
        );
    }
}

