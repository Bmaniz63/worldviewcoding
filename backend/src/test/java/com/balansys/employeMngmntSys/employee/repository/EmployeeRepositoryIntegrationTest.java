package com.balansys.employeMngmntSys.employee.repository;

import com.balansys.employeeMngmntSys.EmployeeMngmntSysApplication;
import com.balansys.employeeMngmntSys.model.entity.Department;
import com.balansys.employeeMngmntSys.model.entity.Employee;
import com.balansys.employeeMngmntSys.repository.DepartmentRepository;
import com.balansys.employeeMngmntSys.repository.EmployeeRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = EmployeeMngmntSysApplication.class)
@Testcontainers
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.open-in-view=false",
        "spring.flyway.enabled=true"
})
class EmployeeRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("employeedb")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

//    @Autowired
//    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // Remove employees first because they reference departments.
        employeeRepository.deleteAll();

        // Now departments can safely be removed.
        departmentRepository.deleteAll();

        // Ensure the case-insensitive email constraint exists.
//        jdbcTemplate.execute("""
//        CREATE UNIQUE INDEX IF NOT EXISTS
//        uk_employees_email_lower
//        ON employees (LOWER(email))
//        """);
    }

    @Test
    void shouldSaveEmployee() {

        Department department =
                departmentRepository.save(
                        new Department("IT")
                );

        Employee employee = new Employee(
                "John",
                "Doe",
                "john.doe@example.com",
                department
        );

        Employee saved =
                employeeRepository.saveAndFlush(employee);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail())
                .isEqualTo("john.doe@example.com");
        assertThat(saved.getDepartment().getName())
                .isEqualTo("IT");
    }

    @Test
    void shouldEnforceCaseInsensitiveEmailUniqueness() {

        Department department =
                departmentRepository.save(
                        new Department("IT")
                );

        Employee firstEmployee = new Employee(
                "John",
                "Doe",
                "john.doe@example.com",
                department
        );

        employeeRepository.saveAndFlush(firstEmployee);

        Employee duplicateEmployee = new Employee(
                "Jane",
                "Doe",
                "JOHN.DOE@EXAMPLE.COM",
                department
        );

        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> employeeRepository.saveAndFlush(
                                duplicateEmployee
                        )
                )
                .isInstanceOf(
                        org.springframework.dao.DataIntegrityViolationException.class
                );
    }

    @Test
    void shouldAllowDifferentEmails() {

        Department department =
                departmentRepository.save(
                        new Department("IT")
                );

        Employee employee1 = new Employee(
                "John",
                "Doe",
                "john.doe@example.com",
                department
        );

        Employee employee2 = new Employee(
                "Jane",
                "Doe",
                "jane.doe@example.com",
                department
        );

        employeeRepository.saveAndFlush(employee1);
        employeeRepository.saveAndFlush(employee2);

        assertThat(employeeRepository.count())
                .isEqualTo(2);
    }
}
