package com.balansys.employeMngmntSys.employee.service;

import com.balansys.employeeMngmntSys.model.dto.EmployeeRequest;
import com.balansys.employeeMngmntSys.model.dto.EmployeeResponse;
import com.balansys.employeeMngmntSys.model.entity.Department;
import com.balansys.employeeMngmntSys.model.entity.Employee;
import com.balansys.employeeMngmntSys.model.exception.DepartmentNotFoundException;
import com.balansys.employeeMngmntSys.model.exception.DuplicateEmailException;
import com.balansys.employeeMngmntSys.model.exception.EmployeeNotFoundException;
import com.balansys.employeeMngmntSys.repository.EmployeeRepository;
import com.balansys.employeeMngmntSys.service.DepartmentService;
import com.balansys.employeeMngmntSys.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private EmployeeService employeeService;

    private Department itDepartment;

    @BeforeEach
    void setUp() {
        itDepartment = new Department(1L, "IT");
    }

    @Test
    void createEmployee_shouldCreateEmployee() {

        EmployeeRequest request = new EmployeeRequest(
                "John",
                "Smith",
                "John.Smith@Example.com",
                1L
        );

        Employee savedEmployee = new Employee(
                100L,
                "John",
                "Smith",
                "john.smith@example.com",
                itDepartment
        );

        when(employeeRepository.existsByEmailIgnoreCase(
                "john.smith@example.com"))
                .thenReturn(false);

        when(departmentService.getDepartmentById(1L))
                .thenReturn(itDepartment);

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(savedEmployee);

        EmployeeResponse response =
                employeeService.createEmployee(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Smith");
        assertThat(response.getEmail())
                .isEqualTo("john.smith@example.com");

        assertThat(response.getDepartment()).isNotNull();
        assertThat(response.getDepartment().getId())
                .isEqualTo(1L);
        assertThat(response.getDepartment().getName())
                .isEqualTo("IT");

        verify(employeeRepository)
                .existsByEmailIgnoreCase(
                        "john.smith@example.com");

        verify(departmentService)
                .getDepartmentById(1L);

        verify(employeeRepository)
                .save(any(Employee.class));
    }

    @Test
    void createEmployee_shouldNormalizeEmail() {

        EmployeeRequest request = new EmployeeRequest(
                "John",
                "Smith",
                "  JOHN.SMITH@EXAMPLE.COM  ",
                1L
        );

        Employee savedEmployee = new Employee(
                100L,
                "John",
                "Smith",
                "john.smith@example.com",
                itDepartment
        );

        when(employeeRepository.existsByEmailIgnoreCase(
                "john.smith@example.com"))
                .thenReturn(false);

        when(departmentService.getDepartmentById(1L))
                .thenReturn(itDepartment);

        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(savedEmployee);

        employeeService.createEmployee(request);

        verify(employeeRepository)
                .existsByEmailIgnoreCase(
                        "john.smith@example.com");
    }

    @Test
    void createEmployee_shouldRejectDuplicateEmail() {

        EmployeeRequest request = new EmployeeRequest(
                "Jane",
                "Doe",
                "john.smith@example.com",
                1L
        );

        when(employeeRepository.existsByEmailIgnoreCase(
                "john.smith@example.com"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                employeeService.createEmployee(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage(
                        "An employee with the email address " +
                                "'john.smith@example.com' already exists"
                );

        verify(employeeRepository)
                .existsByEmailIgnoreCase(
                        "john.smith@example.com");

        verify(departmentService, never())
                .getDepartmentById(anyLong());

        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    @Test
    void createEmployee_shouldRejectMissingDepartment() {

        EmployeeRequest request = new EmployeeRequest(
                "John",
                "Smith",
                "john.smith@example.com",
                99L
        );

        when(employeeRepository.existsByEmailIgnoreCase(
                "john.smith@example.com"))
                .thenReturn(false);

        when(departmentService.getDepartmentById(99L))
                .thenThrow(new DepartmentNotFoundException(99L));

        assertThatThrownBy(() ->
                employeeService.createEmployee(request))
                .isInstanceOf(DepartmentNotFoundException.class)
                .hasMessage("Department 99 does not exist");

        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() {

        Employee employee = new Employee(
                100L,
                "John",
                "Smith",
                "john.smith@example.com",
                itDepartment
        );

        when(employeeRepository.findById(100L))
                .thenReturn(Optional.of(employee));

        EmployeeResponse response =
                employeeService.getEmployeeById(100L);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getEmail())
                .isEqualTo("john.smith@example.com");
        assertThat(response.getDepartment().getName())
                .isEqualTo("IT");
    }

    @Test
    void getEmployeeById_shouldThrowWhenEmployeeDoesNotExist() {

        when(employeeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                employeeService.getEmployeeById(999L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessage("Employee 999 does not exist");
    }

    @Test
    void updateEmployee_shouldUpdateEmployee() {

        Employee existingEmployee = new Employee(
                100L,
                "John",
                "Smith",
                "john.smith@example.com",
                itDepartment
        );

        EmployeeRequest request = new EmployeeRequest(
                "Johnny",
                "Smith",
                "johnny.smith@example.com",
                1L
        );

        when(employeeRepository.findById(100L))
                .thenReturn(Optional.of(existingEmployee));

        when(employeeRepository
                .existsByEmailIgnoreCaseAndIdNot(
                        "johnny.smith@example.com",
                        100L))
                .thenReturn(false);

        when(departmentService.getDepartmentById(1L))
                .thenReturn(itDepartment);

        when(employeeRepository.save(existingEmployee))
                .thenReturn(existingEmployee);

        EmployeeResponse response =
                employeeService.updateEmployee(
                        100L,
                        request
                );

        assertThat(response.getFirstName())
                .isEqualTo("Johnny");

        assertThat(response.getEmail())
                .isEqualTo("johnny.smith@example.com");

        verify(employeeRepository)
                .save(existingEmployee);
    }

    @Test
    void updateEmployee_shouldRejectDuplicateEmail() {

        Employee existingEmployee = new Employee(
                100L,
                "John",
                "Smith",
                "john.smith@example.com",
                itDepartment
        );

        EmployeeRequest request = new EmployeeRequest(
                "John",
                "Smith",
                "jane.doe@example.com",
                1L
        );

        when(employeeRepository.findById(100L))
                .thenReturn(Optional.of(existingEmployee));

        when(employeeRepository
                .existsByEmailIgnoreCaseAndIdNot(
                        "jane.doe@example.com",
                        100L))
                .thenReturn(true);

        assertThatThrownBy(() ->
                employeeService.updateEmployee(
                        100L,
                        request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(employeeRepository, never())
                .save(any(Employee.class));
    }

    @Test
    void updateEmployee_shouldAllowEmployeeToKeepOwnEmail() {

        Employee existingEmployee = new Employee(
                100L,
                "John",
                "Smith",
                "john.smith@example.com",
                itDepartment
        );

        EmployeeRequest request = new EmployeeRequest(
                "Johnny",
                "Smith",
                "JOHN.SMITH@EXAMPLE.COM",
                1L
        );

        when(employeeRepository.findById(100L))
                .thenReturn(Optional.of(existingEmployee));

        when(employeeRepository
                .existsByEmailIgnoreCaseAndIdNot(
                        "john.smith@example.com",
                        100L))
                .thenReturn(false);

        when(departmentService.getDepartmentById(1L))
                .thenReturn(itDepartment);

        when(employeeRepository.save(existingEmployee))
                .thenReturn(existingEmployee);

        EmployeeResponse response =
                employeeService.updateEmployee(
                        100L,
                        request
                );

        assertThat(response.getEmail())
                .isEqualTo("john.smith@example.com");

        assertThat(response.getFirstName())
                .isEqualTo("Johnny");
    }

    @Test
    void deleteEmployee_shouldDeleteEmployee() {

        when(employeeRepository.existsById(100L))
                .thenReturn(true);

        employeeService.deleteEmployee(100L);

        verify(employeeRepository)
                .deleteById(100L);
    }

    @Test
    void deleteEmployee_shouldThrowWhenEmployeeDoesNotExist() {

        when(employeeRepository.existsById(999L))
                .thenReturn(false);

        assertThatThrownBy(() ->
                employeeService.deleteEmployee(999L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessage("Employee 999 does not exist");

        verify(employeeRepository, never())
                .deleteById(anyLong());
    }
}
