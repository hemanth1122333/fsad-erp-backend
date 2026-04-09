package com.education.erp.controller;

import com.education.erp.payload.request.CaptchaVerifyRequest;
import com.education.erp.payload.request.LoginRequest;
import com.education.erp.payload.request.SignupRequest;
import com.education.erp.payload.response.CaptchaChallengeResponse;
import com.education.erp.payload.response.CaptchaVerifyResponse;
import com.education.erp.payload.response.JwtResponse;
import com.education.erp.service.AuthService;
import com.education.erp.service.CaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;
        private final CaptchaService captchaService;

        @GetMapping("/captcha-images")
        public ResponseEntity<CaptchaChallengeResponse> createCaptcha() {
                return ResponseEntity.ok(captchaService.createChallenge());
        }

        @GetMapping(value = "/captcha-images/{sessionId}/{imageId}", produces = "image/svg+xml")
        public ResponseEntity<String> renderCaptchaImage(@PathVariable String sessionId, @PathVariable String imageId) {
                return ResponseEntity.ok(captchaService.renderImage(sessionId, imageId));
        }

        @PostMapping("/verify-captcha")
        public ResponseEntity<CaptchaVerifyResponse> verifyCaptcha(@Valid @RequestBody CaptchaVerifyRequest request) {
                boolean valid = captchaService.verifyChallenge(request.getSessionId(), request.getSelectedImageIds());
                return ResponseEntity.ok(new CaptchaVerifyResponse(valid, valid ? "Captcha verified" : "Captcha is incorrect"));
        }

        @PostMapping("/signup")
        public ResponseEntity<JwtResponse> signup(@Valid @RequestBody SignupRequest request) {
                return ResponseEntity.ok(authService.signup(request));
        }

        @PostMapping("/signin")
        public ResponseEntity<JwtResponse> signin(@Valid @RequestBody LoginRequest request) {
                return ResponseEntity.ok(authService.signin(request));
        }
}
