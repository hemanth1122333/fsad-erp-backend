package com.education.erp.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AttendanceMarkRequest {

    @NotNull
    private Long studentId;

    @NotNull
    private Long classId;

    @NotNull
    private LocalDate attendanceDate;

    private String status;
}