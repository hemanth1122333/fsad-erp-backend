package com.education.erp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceHistoryResponse {
    private Long attendanceId;
    private LocalDate attendanceDate;
    private String status;
    private Long classId;
    private String className;
    private String remarks;
}