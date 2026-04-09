package com.education.erp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a student profile linked to a user account.
 */
@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "admission_number", nullable = false, unique = true, length = 50)
    private String admissionNumber;
    
    @Column(name = "class_name", length = 100)
    private String className;
    
    @Column(name = "batch_year")
    private Integer batchYear;
    
    @Column(length = 30)
    private String phone;
    
    @Column(length = 200)
    private String address;
    
    @Column(name = "guardian_name", length = 120)
    private String guardianName;
}
