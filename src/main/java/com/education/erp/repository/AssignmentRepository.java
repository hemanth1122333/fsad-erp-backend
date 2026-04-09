package com.education.erp.repository;

import com.education.erp.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByClassEntity_Id(Long classId);
    List<Assignment> findByTeacher_Id(Long teacherId);
    Optional<Assignment> findByTitle(String title);
}
