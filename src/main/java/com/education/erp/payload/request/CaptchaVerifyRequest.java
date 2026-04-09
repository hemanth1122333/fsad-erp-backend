package com.education.erp.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CaptchaVerifyRequest {

    @NotBlank
    private String sessionId;

    private List<String> selectedImageIds;
}