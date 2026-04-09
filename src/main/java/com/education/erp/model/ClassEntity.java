package com.education.erp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Represents a class/course (e.g., "Data Structures CS101")
 */
@Entity
@Table(name = "classes")
@Data
@NoArgsConstructor
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "class_code", nullable = false, unique = true, length = 50)
    private String classCode;
    
    @Column(nullable = false, length = 120)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "schedule_day", length = 20)
    private String scheduleDay;
    
    @Column(name = "start_time", length = 10)
    private String startTime;
    
    @Column(name = "end_time", length = 10)
    private String endTime;
    
    @Column(length = 50)
    private String room;
    
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
