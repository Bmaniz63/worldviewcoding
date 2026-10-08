package com.balansys.employeeMngmntSys.model.exception;

public class EmployeeNotFoundException
        extends RuntimeException {

    public EmployeeNotFoundException(Long employeeId) {
        super("Employee " + employeeId + " does not exist");
    }
}


