package com.balansys.employeeMngmntSys.model.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("An employee with the email address '" + email + "' already exists");
    }
}
