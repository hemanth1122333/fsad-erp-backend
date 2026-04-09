package com.education.erp.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StudentRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String admissionNumber;

    private String className;
    private Integer batchYear;
    private String phone;
    private String address;
    private String guardianName;
}