package com.education.erp.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String messageBody;

    private String audienceRole;
    private Boolean active;
}