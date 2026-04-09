package com.education.erp.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequest {
    @NotNull
    private Long senderId;

    @NotNull
    private Long recipientId;

    private String subject;

    @NotBlank
    private String body;
}