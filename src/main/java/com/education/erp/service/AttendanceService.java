package com.education.erp.service;

import com.education.erp.exception.BadRequestException;
import com.education.erp.exception.ResourceNotFoundException;
import com.education.erp.model.Attendance;
import com.education.erp.model.ClassEntity;
import com.education.erp.model.Student;
import com.education.erp.payload.request.AttendanceMarkRequest;
import com.education.erp.payload.response.AttendanceHistoryResponse;
import com.education.erp.payload.response.AttendanceReportResponse;
import com.education.erp.payload.response.AttendanceStudentResponse;
import com.education.erp.repository.AttendanceRepository;
import com.education.erp.repository.ClassEntityRepository;
import com.education.erp.repository.StudentRepository;
import com.education.erp.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private static final double WARNING_THRESHOLD = 75.0;

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final ClassEntityRepository classEntityRepository;

    public List<AttendanceStudentResponse> getStudentsForClass(Long classId, LocalDate attendanceDate) {
        ClassEntity classEntity = classEntityRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

        List<Student> students = resolveStudentsForClass(classEntity);
        Map<Long, AttendanceStats> statsByStudent = buildStatsByStudent();

        return students.stream()
                .sorted(Comparator.comparing(student -> student.getUser().getFullName(), String.CASE_INSENSITIVE_ORDER))
                .map(student -> {
                    Attendance attendance = attendanceDate == null
                            ? null
                            : attendanceRepository.findByStudent_IdAndAttendanceDate(student.getId(), attendanceDate).orElse(null);

                    AttendanceStats stats = statsByStudent.getOrDefault(student.getId(), new AttendanceStats());
                    double percentage = stats.percentage();
                    return new AttendanceStudentResponse(
                            student.getId(),
                            student.getAdmissionNumber(),
                            student.getUser().getFullName(),
                            student.getClassName() != null ? student.getClassName() : classEntity.getName(),
                            student.getBatchYear(),
                            attendance != null && "PRESENT".equalsIgnoreCase(attendance.getStatus()),
                            attendance != null ? attendance.getStatus() : "ABSENT",
                            attendance != null ? attendance.getAttendanceDate() : attendanceDate,
                            stats.presentCount,
                            stats.totalCount,
                            percentage,
                            percentage > 0 && percentage < WARNING_THRESHOLD
                    );
                })
                .toList();
    }

    @Transactional
    public List<AttendanceHistoryResponse> markAttendance(List<AttendanceMarkRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new BadRequestException("Attendance list cannot be empty");
        }

        List<AttendanceHistoryResponse> savedRecords = new ArrayList<>();
        for (AttendanceMarkRequest request : requests) {
            String normalizedStatus = normalizeStatus(request.getStatus());
            Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            ClassEntity classEntity = classEntityRepository.findById(request.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

            Attendance attendance = attendanceRepository.findByStudent_IdAndAttendanceDate(student.getId(), request.getAttendanceDate())
                    .orElseGet(Attendance::new);

            attendance.setStudent(student);
            attendance.setClassEntity(classEntity);
            attendance.setAttendanceDate(request.getAttendanceDate());
            attendance.setStatus(normalizedStatus);
            attendance.setRemarks("PRESENT".equals(normalizedStatus) ? "Marked from attendance sheet" : "Marked absent");

            Attendance saved = attendanceRepository.save(attendance);
            savedRecords.add(toHistoryResponse(saved));
        }

        return savedRecords;
    }

    public AttendanceReportResponse getReport(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<Attendance> records = attendanceRepository.findByStudent_IdOrderByAttendanceDateDesc(studentId);
        long totalClasses = records.size();
        long classesAttended = records.stream()
                .filter(record -> "PRESENT".equalsIgnoreCase(record.getStatus()))
                .count();
        double percentage = totalClasses == 0 ? 0.0 : roundToOneDecimal((classesAttended * 100.0) / totalClasses);

        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        statusBreakdown.put("PRESENT", classesAttended);
        statusBreakdown.put("ABSENT", totalClasses - classesAttended);

        List<AttendanceHistoryResponse> history = records.stream()
                .map(this::toHistoryResponse)
                .toList();

        YearMonth currentMonth = YearMonth.now();
        List<AttendanceHistoryResponse> currentMonthHistory = records.stream()
                .filter(record -> YearMonth.from(record.getAttendanceDate()).equals(currentMonth))
                .map(this::toHistoryResponse)
                .toList();

        Map<Long, Double> ranking = calculateRanking();
        long rank = ranking.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .indexOf(studentId) + 1L;

        String warningMessage = percentage < WARNING_THRESHOLD
                ? "Attendance is below 75%. Please review this student and notify guardians if needed."
                : "Attendance is healthy.";

        return new AttendanceReportResponse(
                student.getId(),
                student.getUser().getFullName(),
                student.getAdmissionNumber(),
                student.getClassName(),
            student.getBatchYear(),
                classesAttended,
                totalClasses,
                percentage,
                rank,
                studentRepository.count(),
                percentage < WARNING_THRESHOLD,
                warningMessage,
                statusBreakdown,
                history,
                currentMonthHistory
        );
    }

    public AttendanceReportResponse getCurrentUserReport(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Student student = studentRepository.findByUser_Id(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for the current user"));
        return getReport(student.getId());
    }

    private List<Student> resolveStudentsForClass(ClassEntity classEntity) {
        String className = classEntity.getName();
        String classCode = classEntity.getClassCode();
        List<Student> matchedStudents = studentRepository.findAll().stream()
                .filter(student -> student.getClassName() != null && (
                        student.getClassName().equalsIgnoreCase(className)
                                || student.getClassName().equalsIgnoreCase(classCode)
                                || student.getClassName().toLowerCase(Locale.ROOT).contains(className.toLowerCase(Locale.ROOT))))
                .collect(Collectors.toList());
        return matchedStudents.isEmpty() ? studentRepository.findAll() : matchedStudents;
    }

    private Map<Long, AttendanceStats> buildStatsByStudent() {
        Map<Long, AttendanceStats> stats = new HashMap<>();
        for (Attendance record : attendanceRepository.findAll()) {
            AttendanceStats studentStats = stats.computeIfAbsent(record.getStudent().getId(), key -> new AttendanceStats());
            studentStats.totalCount++;
            if ("PRESENT".equalsIgnoreCase(record.getStatus())) {
                studentStats.presentCount++;
            }
        }
        return stats;
    }

    private Map<Long, Double> calculateRanking() {
        Map<Long, AttendanceStats> statsByStudent = buildStatsByStudent();
        return studentRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Student::getId,
                        student -> statsByStudent.getOrDefault(student.getId(), new AttendanceStats()).percentage()
                ));
    }

    private AttendanceHistoryResponse toHistoryResponse(Attendance attendance) {
        return new AttendanceHistoryResponse(
                attendance.getId(),
                attendance.getAttendanceDate(),
                attendance.getStatus(),
                attendance.getClassEntity() != null ? attendance.getClassEntity().getId() : null,
                attendance.getClassEntity() != null ? attendance.getClassEntity().getName() : null,
                attendance.getRemarks()
        );
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "PRESENT";
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!"PRESENT".equals(normalized) && !"ABSENT".equals(normalized)) {
            throw new BadRequestException("Attendance status must be PRESENT or ABSENT");
        }
        return normalized;
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static class AttendanceStats {
        private long presentCount;
        private long totalCount;

        private double percentage() {
            return totalCount == 0 ? 0.0 : Math.round(((presentCount * 100.0) / totalCount) * 10.0) / 10.0;
        }
    }
}