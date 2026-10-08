package com.balansys.employeeMngmntSys.config;

import com.balansys.employeeMngmntSys.model.entity.Department;
import com.balansys.employeeMngmntSys.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class DepartmentDataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) {

        List<String> departments = List.of(
                "IT",
                "Human Resources",
                "Finance",
                "Accounting",
                "Marketing",
                "Sales",
                "Operations"
        );

        for (String departmentName : departments) {

            if (departmentRepository
                    .findByName(departmentName)
                    .isEmpty()) {

                departmentRepository.save(
                        new Department(departmentName)
                );
            }
        }
    }
}
