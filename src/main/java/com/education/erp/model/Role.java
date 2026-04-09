package com.education.erp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the roles available in the system:
 * ADMIN, TEACHER, STUDENT, ADMINISTRATOR
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {

    // Primary Key for the role table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Role name, e.g., "ROLE_ADMIN". It must be unique.
    @Column(unique = true, nullable = false, length = 50)
    private String name;
    
    // Optional parameterised constructor
    public Role(String name) {
        this.name = name;
    }
}
