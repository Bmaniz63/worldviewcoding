package com.balansys.employeeMngmntSys.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn( name = "department_id", nullable = false)
    private Department department;

    public Employee(
            String firstName,
            String lastName,
            String email,
            Department department) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
    }
}

