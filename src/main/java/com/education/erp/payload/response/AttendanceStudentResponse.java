package com.education.erp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStudentResponse {
    private Long studentId;
    private String admissionNumber;
    private String studentName;
    private String className;
    private Integer batchYear;
    private boolean present;
    private String status;
    private LocalDate attendanceDate;
    private long classesAttended;
    private long totalClasses;
    private double attendancePercentage;
    private boolean lowAttendance;
}