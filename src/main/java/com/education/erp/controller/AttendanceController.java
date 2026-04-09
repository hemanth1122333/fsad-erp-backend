package com.education.erp.controller;

import com.education.erp.payload.request.AttendanceMarkRequest;
import com.education.erp.payload.response.AttendanceHistoryResponse;
import com.education.erp.payload.response.AttendanceReportResponse;
import com.education.erp.payload.response.AttendanceStudentResponse;
import com.education.erp.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/students")
    public List<AttendanceStudentResponse> students(
            @RequestParam Long classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return attendanceService.getStudentsForClass(classId, date);
    }

    @PostMapping("/mark")
    public List<AttendanceHistoryResponse> markAttendance(@Valid @RequestBody List<AttendanceMarkRequest> requests) {
        return attendanceService.markAttendance(requests);
    }

    @GetMapping("/report/{studentId}")
    public AttendanceReportResponse report(@PathVariable Long studentId) {
        return attendanceService.getReport(studentId);
    }

    @GetMapping("/report/me")
    public AttendanceReportResponse currentUserReport(Authentication authentication) {
        return attendanceService.getCurrentUserReport(authentication);
    }
}