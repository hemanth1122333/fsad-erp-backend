package com.education.erp.repository;

import com.education.erp.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudent_Id(Long studentId);
    List<Grade> findByClassEntity_Id(Long classId);
    Optional<Grade> findByStudent_IdAndSubjectAndAssessmentName(Long studentId, String subject, String assessmentName);
    
    @Query("SELECT g FROM Grade g WHERE g.student.id = :studentId AND g.classEntity.id = :classId AND g.assessmentName = :assessmentName")
    Optional<Grade> findByStudentIdAndClassIdAndAssessmentName(
        @Param("studentId") Long studentId, 
        @Param("classId") Long classId, 
        @Param("assessmentName") String assessmentName);
}
