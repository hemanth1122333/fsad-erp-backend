package com.education.erp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaVerifyResponse {
    private boolean valid;
    private String message;
}