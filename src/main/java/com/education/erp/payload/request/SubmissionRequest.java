package com.education.erp.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmissionRequest {
    @NotNull
    private Long assignmentId;

    @NotNull
    private Long studentId;

    private String filePath;
    private String remarks;
    private String status;
}