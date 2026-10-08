package com.balansys.employeMngmntSys.employee.controller;

import com.balansys.employeeMngmntSys.controller.EmployeeController;
import com.balansys.employeeMngmntSys.model.dto.DepartmentResponse;
import com.balansys.employeeMngmntSys.model.dto.EmployeeResponse;
import com.balansys.employeeMngmntSys.model.exception.DepartmentNotFoundException;
import com.balansys.employeeMngmntSys.model.exception.DuplicateEmailException;
import com.balansys.employeeMngmntSys.model.exception.EmployeeNotFoundException;
import com.balansys.employeeMngmntSys.model.exception.GlobalExceptionHandler;
import com.balansys.employeeMngmntSys.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeController)
                .setControllerAdvice(
                        new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getEmployees_shouldReturn200() throws Exception {

        EmployeeResponse employee =
                new EmployeeResponse(
                        1L,
                        "John",
                        "Smith",
                        "john@example.com",
                        new DepartmentResponse(
                                1L,
                                "IT"
                        )
                );

        when(employeeService.getAllEmployees())
                .thenReturn(List.of(employee));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id")
                        .value(1))
                .andExpect(jsonPath("$[0].firstName")
                        .value("John"))
                .andExpect(jsonPath("$[0].lastName")
                        .value("Smith"))
                .andExpect(jsonPath("$[0].email")
                        .value("john@example.com"))
                .andExpect(jsonPath("$[0].department.id")
                        .value(1))
                .andExpect(jsonPath("$[0].department.name")
                        .value("IT"));
    }

    @Test
    void getEmployee_shouldReturn200() throws Exception {

        EmployeeResponse employee =
                new EmployeeResponse(
                        1L,
                        "John",
                        "Smith",
                        "john@example.com",
                        new DepartmentResponse(
                                1L,
                                "IT"
                        )
                );

        when(employeeService.getEmployeeById(1L))
                .thenReturn(employee);

        mockMvc.perform(
                        get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.email")
                        .value("john@example.com"));
    }

    @Test
    void getEmployee_shouldReturn404() throws Exception {

        when(employeeService.getEmployeeById(999L))
                .thenThrow(
                        new EmployeeNotFoundException(999L));

        mockMvc.perform(
                        get("/api/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.error")
                        .value("Employee Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Employee 999 does not exist"));
    }

    @Test
    void createEmployee_shouldReturn201() throws Exception {

        String requestBody = """
                {
                    "firstName": "John",
                    "lastName": "Smith",
                    "email": "john@example.com",
                    "departmentId": 1
                }
                """;

        EmployeeResponse response =
                new EmployeeResponse(
                        1L,
                        "John",
                        "Smith",
                        "john@example.com",
                        new DepartmentResponse(
                                1L,
                                "IT"
                        )
                );

        when(employeeService.createEmployee(any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id")
                        .value(1))
                .andExpect(jsonPath("$.email")
                        .value("john@example.com"));
    }

    @Test
    void createEmployee_shouldReturn400ForInvalidRequest()
            throws Exception {

        String requestBody = """
                {
                    "firstName": "",
                    "lastName": "",
                    "email": "not-an-email",
                    "departmentId": null
                }
                """;

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.error")
                        .value("Validation Failed"))
                .andExpect(jsonPath(
                        "$.validationErrors.firstName")
                        .value("First name is required"))
                .andExpect(jsonPath(
                        "$.validationErrors.lastName")
                        .value("Last name is required"))
                .andExpect(jsonPath(
                        "$.validationErrors.departmentId")
                        .value("Department ID is required"));

        verify(employeeService, never())
                .createEmployee(any());
    }

    @Test
    void createEmployee_shouldReturn409ForDuplicateEmail()
            throws Exception {

        String requestBody = """
                {
                    "firstName": "John",
                    "lastName": "Smith",
                    "email": "john@example.com",
                    "departmentId": 1
                }
                """;

        when(employeeService.createEmployee(any()))
                .thenThrow(
                        new DuplicateEmailException(
                                "john@example.com"));

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.error")
                        .value("Duplicate Email"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "An employee with the email address " +
                                        "'john@example.com' already exists"
                        ));
    }

    @Test
    void createEmployee_shouldReturn404ForMissingDepartment()
            throws Exception {

        String requestBody = """
                {
                    "firstName": "John",
                    "lastName": "Smith",
                    "email": "john@example.com",
                    "departmentId": 999
                }
                """;

        when(employeeService.createEmployee(any()))
                .thenThrow(
                        new DepartmentNotFoundException(999L));

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.error")
                        .value("Department Not Found"));
    }

    @Test
    void updateEmployee_shouldReturn200() throws Exception {

        String requestBody = """
                {
                    "firstName": "Johnny",
                    "lastName": "Smith",
                    "email": "johnny@example.com",
                    "departmentId": 1
                }
                """;

        EmployeeResponse response =
                new EmployeeResponse(
                        1L,
                        "Johnny",
                        "Smith",
                        "johnny@example.com",
                        new DepartmentResponse(
                                1L,
                                "IT"
                        )
                );

        when(employeeService.updateEmployee(
                eq(1L),
                any()))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/employees/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName")
                        .value("Johnny"))
                .andExpect(jsonPath("$.email")
                        .value("johnny@example.com"));
    }

    @Test
    void updateEmployee_shouldReturn404()
            throws Exception {

        String requestBody = """
                {
                    "firstName": "John",
                    "lastName": "Smith",
                    "email": "john@example.com",
                    "departmentId": 1
                }
                """;

        when(employeeService.updateEmployee(
                eq(999L),
                any()))
                .thenThrow(
                        new EmployeeNotFoundException(999L));

        mockMvc.perform(
                        put("/api/employees/999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404));
    }

    @Test
    void deleteEmployee_shouldReturn204()
            throws Exception {

        doNothing()
                .when(employeeService)
                .deleteEmployee(1L);

        mockMvc.perform(
                        delete("/api/employees/1"))
                .andExpect(status().isNoContent());

        verify(employeeService)
                .deleteEmployee(1L);
    }

    @Test
    void deleteEmployee_shouldReturn404()
            throws Exception {

        doThrow(
                new EmployeeNotFoundException(999L))
                .when(employeeService)
                .deleteEmployee(999L);

        mockMvc.perform(
                        delete("/api/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404));
    }
}
