package com.education.erp.repository;

import com.education.erp.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {
    List<ClassEntity> findByTeacher_Id(Long teacherId);
    Optional<ClassEntity> findByClassCode(String classCode);
}
