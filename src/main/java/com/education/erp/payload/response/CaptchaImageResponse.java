package com.education.erp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaImageResponse {
    private String id;
    private String url;
}