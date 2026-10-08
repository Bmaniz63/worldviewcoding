package com.balansys.employeeMngmntSys.model.exception;

public class DepartmentNotFoundException
        extends RuntimeException {

    public DepartmentNotFoundException(Long departmentId) {
        super("Department " + departmentId + " does not exist");
    }
}


