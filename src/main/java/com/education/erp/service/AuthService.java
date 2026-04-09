package com.education.erp.service;

import com.education.erp.exception.BadRequestException;
import com.education.erp.model.Role;
import com.education.erp.model.Student;
import com.education.erp.model.Teacher;
import com.education.erp.model.User;
import com.education.erp.payload.request.LoginRequest;
import com.education.erp.payload.request.SignupRequest;
import com.education.erp.payload.response.JwtResponse;
import com.education.erp.repository.RoleRepository;
import com.education.erp.repository.StudentRepository;
import com.education.erp.repository.TeacherRepository;
import com.education.erp.repository.UserRepository;
import com.education.erp.security.jwt.JwtUtils;
import com.education.erp.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final CaptchaService captchaService;
    private final ActivityLogService activityLogService;

    public JwtResponse signup(SignupRequest request) {
        validateCaptcha(request.getCaptchaSessionId(), request.getSelectedImageIds());
        validateSignupInput(request.getName(), request.getEmail(), request.getPassword(), request.getRole());

        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email is already registered");
        }

        Role role = roleRepository.findByNameIgnoreCase(normalizeRole(request.getRole()))
                .orElseThrow(() -> new BadRequestException("Role not found"));

        User user = new User();
        user.setFullName(request.getName().trim());
        user.setEmail(email);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(role);
        user = userRepository.save(user);

        createProfileIfNeeded(user, role.getName());
        activityLogService.record(email, "SIGN_UP", "User", String.valueOf(user.getId()), "New account created");

        return buildTokenResponse(user);
    }

    public JwtResponse signin(LoginRequest request) {
        validateCaptcha(request.getCaptchaSessionId(), request.getSelectedImageIds());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(Locale.ROOT), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        activityLogService.record(userDetails.getEmail(), "SIGN_IN", "User", String.valueOf(userDetails.getId()), "User logged in");
        return buildTokenResponse(userDetails, authentication);
    }

    private JwtResponse buildTokenResponse(User user) {
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        return buildTokenResponse(userDetails, authentication);
    }

    private JwtResponse buildTokenResponse(UserDetailsImpl userDetails, Authentication authentication) {
        String token = jwtUtils.generateJwtToken(authentication);
        List<String> roles = userDetails.getAuthorities().stream().map(authority -> authority.getAuthority()).collect(Collectors.toList());
        return new JwtResponse(token, userDetails.getId(), userDetails.getFullName(), userDetails.getEmail(), roles, resolveHomeRoute(roles));
    }

    private void validateCaptcha(String sessionId, List<String> selectedImageIds) {
        if (!captchaService.verifyChallenge(sessionId, selectedImageIds)) {
            throw new BadRequestException("CAPTCHA validation failed");
        }
    }

    private void validateSignupInput(String name, String email, String password, String role) {
        if (name == null || name.trim().length() < 2) {
            throw new BadRequestException("Name must contain at least 2 characters");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new BadRequestException("Please enter a valid email address");
        }
        if (password == null || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")) {
            throw new BadRequestException("Password must be 8+ characters and include upper, lower, number, and special character");
        }
        if (role == null || role.isBlank()) {
            throw new BadRequestException("Role is required");
        }
    }

    private String normalizeRole(String role) {
        String normalized = role.trim().toUpperCase(Locale.ROOT).replace("ROLE_", "");
        return "ROLE_" + normalized;
    }

    private void createProfileIfNeeded(User user, String roleName) {
        if ("ROLE_STUDENT".equalsIgnoreCase(roleName)) {
            Student student = new Student();
            student.setUser(user);
            student.setAdmissionNumber("STD-" + shortId());
            student.setClassName("New Intake");
            student.setBatchYear(Year.now().getValue());
            studentRepository.save(student);
            return;
        }

        if ("ROLE_TEACHER".equalsIgnoreCase(roleName)) {
            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setEmployeeCode("TCH-" + shortId());
            teacher.setDepartment("General");
            teacherRepository.save(teacher);
        }
    }

    private String shortId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private String resolveHomeRoute(List<String> roles) {
        if (roles.contains("ROLE_ADMINISTRATOR")) {
            return "/dashboard/administrator";
        }
        if (roles.contains("ROLE_ADMIN")) {
            return "/dashboard/admin";
        }
        if (roles.contains("ROLE_TEACHER")) {
            return "/dashboard/teacher";
        }
        return "/dashboard/student";
    }
}