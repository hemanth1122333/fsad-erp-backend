package com.education.erp.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GradeRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private Long classId;

    private String subject;
    private String assessmentType;
    private String assessmentName;
    private BigDecimal marksObtained;
    private BigDecimal maxMarks;
    private String gradeLetter;
    private String remark;
}