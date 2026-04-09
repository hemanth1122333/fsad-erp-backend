package com.education.erp.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClassRequest {
    @NotBlank
    private String classCode;

    @NotBlank
    private String name;

    private String description;
    private String scheduleDay;
    private String startTime;
    private String endTime;
    private String room;
    private Long teacherId;
}