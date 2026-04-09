package com.education.erp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportResponse {
    private Long studentId;
    private String studentName;
    private String admissionNumber;
    private String className;
    private Integer batchYear;
    private long classesAttended;
    private long totalClasses;
    private double attendancePercentage;
    private long rank;
    private long totalStudents;
    private boolean warning;
    private String warningMessage;
    private Map<String, Long> statusBreakdown;
    private List<AttendanceHistoryResponse> history;
    private List<AttendanceHistoryResponse> currentMonthHistory;
}