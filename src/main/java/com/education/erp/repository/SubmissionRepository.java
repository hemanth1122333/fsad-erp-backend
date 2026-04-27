package com.education.erp.repository;

import com.education.erp.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByStudent_IdOrderBySubmittedAtDesc(Long studentId);
    Optional<Submission> findByAssignment_IdAndStudent_Id(Long assignmentId, Long studentId);
}
