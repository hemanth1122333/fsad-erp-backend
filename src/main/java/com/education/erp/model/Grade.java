package com.education.erp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stores grades/marks obtained by a student in a specific class assessment.
 */
@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;
    
    @Column(length = 120, nullable = false)
    private String subject;
    
    @Column(name = "assessment_name", nullable = false, length = 120)
    private String assessmentName;
    
    @Column(name = "marks_obtained", precision = 6, scale = 2, nullable = false)
    private BigDecimal marksObtained;
    
    @Column(name = "max_marks", precision = 6, scale = 2, nullable = false)
    private BigDecimal maxMarks;
    
    @Column(name = "grade_letter", length = 5)
    private String gradeLetter;
    
    @Column(columnDefinition = "TEXT")
    private String remark;
    
    @Column(name = "recorded_at", insertable = false, updatable = false)
    private LocalDateTime recordedAt;
}
