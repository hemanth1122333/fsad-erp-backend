package com.education.erp.payload.response;

import lombok.Data;
import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private List<String> roles;
    private String homeRoute;

    public JwtResponse(String accessToken, Long id, String name, String email, List<String> roles, String homeRoute) {
        this.token = accessToken;
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.homeRoute = homeRoute;
    }
}
