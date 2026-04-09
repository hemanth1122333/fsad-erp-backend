package com.education.erp.repository;

import com.education.erp.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    // Find all attendance records for a specific student
    List<Attendance> findByStudent_Id(Long studentId);

    List<Attendance> findByStudent_IdOrderByAttendanceDateDesc(Long studentId);

    List<Attendance> findByStudent_IdAndAttendanceDateBetweenOrderByAttendanceDateDesc(Long studentId, LocalDate startDate, LocalDate endDate);

    Optional<Attendance> findByStudent_IdAndAttendanceDate(Long studentId, LocalDate attendanceDate);
    
    // Find all attendance records for a specific class (used by teacher)
    List<Attendance> findByClassEntity_Id(Long classId);

    List<Attendance> findByClassEntity_IdAndAttendanceDate(Long classId, LocalDate attendanceDate);
    
    // Find specific attendance record
    @Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.classEntity.id = :classId AND a.attendanceDate = :date")
    Optional<Attendance> findByStudentIdAndClassIdAndAttendanceDate(
        @Param("studentId") Long studentId, 
        @Param("classId") Long classId, 
        @Param("date") LocalDate date);
}
