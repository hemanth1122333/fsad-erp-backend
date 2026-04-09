package com.education.erp.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CaptchaChallengeResponse {
    private String sessionId;
    private String question;
    private List<CaptchaImageResponse> images;
}