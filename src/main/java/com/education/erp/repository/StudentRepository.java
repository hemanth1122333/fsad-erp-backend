package com.education.erp.repository;

import com.education.erp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUser_Id(Long userId);
    Optional<Student> findByAdmissionNumber(String admissionNumber);
    
    // Search methods
    @Query("SELECT s FROM Student s WHERE LOWER(s.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(s.admissionNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Student> searchByNameOrAdmission(@Param("search") String search);
    
    List<Student> findByClassName(String className);
    List<Student> findByBatchYear(Integer batchYear);
}
