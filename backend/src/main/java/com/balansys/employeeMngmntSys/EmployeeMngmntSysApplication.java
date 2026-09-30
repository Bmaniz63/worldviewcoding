package com.balansys.employeeMngmntSys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmployeeMngmntSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeMngmntSysApplication.class, args);

        System.out.println("");  //add a blank line for better readability in the console.
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println("Employee-Management-System backend started successfully... :-)");
        System.out.println("---------------------------------------------------------------------------------------------");

    }

}
