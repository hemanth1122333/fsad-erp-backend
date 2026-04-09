package com.education.erp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a teacher profile linked to a user account.
 */
@Entity
@Table(name = "teachers")
@Data
@NoArgsConstructor
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "employee_code", nullable = false, unique = true, length = 50)
    private String employeeCode;
    
    @Column(length = 120)
    private String department;
    
    @Column(length = 30)
    private String phone;
    
    @Column(name = "office_room", length = 50)
    private String officeRoom;
}
