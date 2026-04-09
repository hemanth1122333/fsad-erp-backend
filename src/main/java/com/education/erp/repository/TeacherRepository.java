package com.education.erp.repository;

import com.education.erp.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUser_Id(Long userId);
    Optional<Teacher> findByEmployeeCode(String employeeCode);
    
    // Search methods
    @Query("SELECT t FROM Teacher t WHERE LOWER(t.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.employeeCode) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Teacher> searchByNameOrEmployee(@Param("search") String search);
    
    List<Teacher> findByDepartment(String department);
}
