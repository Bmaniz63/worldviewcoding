package com.balansys.employeMngmntSys;

import com.balansys.employeeMngmntSys.EmployeeMngmntSysApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = EmployeeMngmntSysApplication.class, properties = "spring.flyway.enabled=false")
class EmployeeMngmntSysApplicationTests {

    @Test
    void contextLoads() {
    }

}
