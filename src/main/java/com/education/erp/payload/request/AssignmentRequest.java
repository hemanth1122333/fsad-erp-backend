package com.education.erp.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentRequest {
    @NotNull
    private Long classId;

    @NotNull
    private Long teacherId;

    @NotBlank
    private String title;

    private String description;
    private LocalDateTime dueDate;
    private String filePath;
}