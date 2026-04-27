package com.education.erp.controller;

import com.education.erp.exception.ResourceNotFoundException;
import com.education.erp.model.Assignment;
import com.education.erp.model.ClassEntity;
import com.education.erp.model.Grade;
import com.education.erp.model.Message;
import com.education.erp.model.Notification;
import com.education.erp.model.Role;
import com.education.erp.model.Student;
import com.education.erp.model.Submission;
import com.education.erp.model.Teacher;
import com.education.erp.model.User;
import com.education.erp.payload.request.AssignmentRequest;
import com.education.erp.payload.request.ClassRequest;
import com.education.erp.payload.request.GradeRequest;
import com.education.erp.payload.request.MessageRequest;
import com.education.erp.payload.request.NotificationRequest;
import com.education.erp.payload.request.StudentRequest;
import com.education.erp.payload.request.SubmissionRequest;
import com.education.erp.payload.request.TeacherRequest;
import com.education.erp.repository.AssignmentRepository;
import com.education.erp.repository.ClassEntityRepository;
import com.education.erp.repository.GradeRepository;
import com.education.erp.repository.MessageRepository;
import com.education.erp.repository.ActivityLogRepository;
import com.education.erp.repository.NotificationRepository;
import com.education.erp.repository.RoleRepository;
import com.education.erp.repository.StudentRepository;
import com.education.erp.repository.SubmissionRepository;
import com.education.erp.repository.TeacherRepository;
import com.education.erp.repository.UserRepository;
import com.education.erp.service.ActivityLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ErpController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassEntityRepository classEntityRepository;
    private final GradeRepository gradeRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final MessageRepository messageRepository;
    private final ActivityLogRepository activityLogRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    @GetMapping("/students")
    public List<Student> students() {
        return studentRepository.findAll();
    }

    @PostMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public Student createStudent(@Valid @RequestBody StudentRequest request) {
        User user = createAccount(request.getName(), request.getEmail(), request.getPassword(), "ROLE_STUDENT");
        Student student = new Student();
        student.setUser(user);
        student.setAdmissionNumber(request.getAdmissionNumber());
        student.setClassName(request.getClassName());
        student.setBatchYear(request.getBatchYear());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());
        student.setGuardianName(request.getGuardianName());
        Student saved = studentRepository.save(student);
        log("CREATE", "Student", String.valueOf(saved.getId()), saved.getAdmissionNumber());
        return saved;
    }

    @PutMapping("/students/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Student updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        User user = student.getUser();
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        student.setAdmissionNumber(request.getAdmissionNumber());
        student.setClassName(request.getClassName());
        student.setBatchYear(request.getBatchYear());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());
        student.setGuardianName(request.getGuardianName());
        Student saved = studentRepository.save(student);
        log("UPDATE", "Student", String.valueOf(saved.getId()), saved.getAdmissionNumber());
        return saved;
    }

    @DeleteMapping("/students/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        userRepository.delete(student.getUser());
        studentRepository.delete(student);
        log("DELETE", "Student", String.valueOf(id), student.getAdmissionNumber());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/teachers")
    public List<Teacher> teachers() {
        return teacherRepository.findAll();
    }

    @PostMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public Teacher createTeacher(@Valid @RequestBody TeacherRequest request) {
        User user = createAccount(request.getName(), request.getEmail(), request.getPassword(), "ROLE_TEACHER");
        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setEmployeeCode(request.getEmployeeCode());
        teacher.setDepartment(request.getDepartment());
        teacher.setPhone(request.getPhone());
        teacher.setOfficeRoom(request.getOfficeRoom());
        Teacher saved = teacherRepository.save(teacher);
        log("CREATE", "Teacher", String.valueOf(saved.getId()), saved.getEmployeeCode());
        return saved;
    }

    @PutMapping("/teachers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Teacher updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        User user = teacher.getUser();
        user.setFullName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        teacher.setEmployeeCode(request.getEmployeeCode());
        teacher.setDepartment(request.getDepartment());
        teacher.setPhone(request.getPhone());
        teacher.setOfficeRoom(request.getOfficeRoom());
        Teacher saved = teacherRepository.save(teacher);
        log("UPDATE", "Teacher", String.valueOf(saved.getId()), saved.getEmployeeCode());
        return saved;
    }

    @DeleteMapping("/teachers/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        Teacher teacher = teacherRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        userRepository.delete(teacher.getUser());
        teacherRepository.delete(teacher);
        log("DELETE", "Teacher", String.valueOf(id), teacher.getEmployeeCode());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/classes")
    public List<ClassEntity> classes() {
        return classEntityRepository.findAll();
    }

    @PostMapping("/classes")
    public ClassEntity createClass(@Valid @RequestBody ClassRequest request) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setClassCode(request.getClassCode());
        classEntity.setName(request.getName());
        classEntity.setDescription(request.getDescription());
        classEntity.setScheduleDay(request.getScheduleDay());
        classEntity.setStartTime(request.getStartTime());
        classEntity.setEndTime(request.getEndTime());
        classEntity.setRoom(request.getRoom());
        if (request.getTeacherId() != null) {
            classEntity.setTeacher(teacherRepository.findById(request.getTeacherId()).orElseThrow(() -> new ResourceNotFoundException("Teacher not found")));
        }
        ClassEntity saved = classEntityRepository.save(classEntity);
        log("CREATE", "Class", String.valueOf(saved.getId()), saved.getClassCode());
        return saved;
    }

    @PutMapping("/classes/{id}")
    public ClassEntity updateClass(@PathVariable Long id, @Valid @RequestBody ClassRequest request) {
        ClassEntity classEntity = classEntityRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        classEntity.setClassCode(request.getClassCode());
        classEntity.setName(request.getName());
        classEntity.setDescription(request.getDescription());
        classEntity.setScheduleDay(request.getScheduleDay());
        classEntity.setStartTime(request.getStartTime());
        classEntity.setEndTime(request.getEndTime());
        classEntity.setRoom(request.getRoom());
        if (request.getTeacherId() != null) {
            classEntity.setTeacher(teacherRepository.findById(request.getTeacherId()).orElseThrow(() -> new ResourceNotFoundException("Teacher not found")));
        }
        ClassEntity saved = classEntityRepository.save(classEntity);
        log("UPDATE", "Class", String.valueOf(saved.getId()), saved.getClassCode());
        return saved;
    }

    @DeleteMapping("/classes/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        classEntityRepository.deleteById(id);
        log("DELETE", "Class", String.valueOf(id), "Removed class");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grades")
    public List<Grade> grades() {
        com.education.erp.security.services.UserDetailsImpl principal = currentPrincipal();
        if (principal != null && hasAuthority(principal, "ROLE_STUDENT")) {
            Student student = studentRepository.findByUser_Id(principal.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            return gradeRepository.findByStudent_Id(student.getId());
        }
        return gradeRepository.findAll();
    }

    @PostMapping("/grades")
    @PreAuthorize("hasRole('TEACHER')")
    public Grade createGrade(@Valid @RequestBody GradeRequest request) {
        Grade grade = new Grade();
        grade.setStudent(studentRepository.findById(request.getStudentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found")));
        grade.setClassEntity(classEntityRepository.findById(request.getClassId()).orElseThrow(() -> new ResourceNotFoundException("Class not found")));
        grade.setSubject(request.getSubject());
        grade.setAssessmentType(request.getAssessmentType() != null ? request.getAssessmentType() : "INTERNAL");
        grade.setAssessmentName(request.getAssessmentName());
        grade.setMarksObtained(request.getMarksObtained());
        grade.setMaxMarks(request.getMaxMarks());
        grade.setGradeLetter(request.getGradeLetter());
        grade.setRemark(request.getRemark());
        Grade saved = gradeRepository.save(grade);
        log("CREATE", "Grade", String.valueOf(saved.getId()), saved.getAssessmentName());
        return saved;
    }

    @PutMapping("/grades/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Grade updateGrade(@PathVariable Long id, @Valid @RequestBody GradeRequest request) {
        Grade grade = gradeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Grade not found"));
        grade.setStudent(studentRepository.findById(request.getStudentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found")));
        grade.setClassEntity(classEntityRepository.findById(request.getClassId()).orElseThrow(() -> new ResourceNotFoundException("Class not found")));
        grade.setSubject(request.getSubject());
        grade.setAssessmentType(request.getAssessmentType() != null ? request.getAssessmentType() : grade.getAssessmentType());
        grade.setAssessmentName(request.getAssessmentName());
        grade.setMarksObtained(request.getMarksObtained());
        grade.setMaxMarks(request.getMaxMarks());
        grade.setGradeLetter(request.getGradeLetter());
        grade.setRemark(request.getRemark());
        Grade saved = gradeRepository.save(grade);
        log("UPDATE", "Grade", String.valueOf(saved.getId()), saved.getAssessmentName());
        return saved;
    }

    @DeleteMapping("/grades/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
        gradeRepository.deleteById(id);
        log("DELETE", "Grade", String.valueOf(id), "Removed grade");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assignments")
    public List<Assignment> assignments() {
        com.education.erp.security.services.UserDetailsImpl principal = currentPrincipal();
        if (principal != null && hasAuthority(principal, "ROLE_STUDENT")) {
            Student student = currentStudent(principal);
            List<Assignment> matchingAssignments = assignmentRepository.findAll().stream()
                    .filter(assignment -> assignmentMatchesStudent(assignment, student))
                    .toList();
            return matchingAssignments.isEmpty() ? assignmentRepository.findAll() : matchingAssignments;
        }
        return assignmentRepository.findAll();
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Assignment createAssignment(@Valid @RequestBody AssignmentRequest request) {
        Assignment assignment = new Assignment();
        assignment.setClassEntity(classEntityRepository.findById(request.getClassId()).orElseThrow(() -> new ResourceNotFoundException("Class not found")));
        assignment.setTeacher(teacherRepository.findById(request.getTeacherId()).orElseThrow(() -> new ResourceNotFoundException("Teacher not found")));
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setFilePath(request.getFilePath());
        Assignment saved = assignmentRepository.save(assignment);
        log("CREATE", "Assignment", String.valueOf(saved.getId()), saved.getTitle());
        return saved;
    }

    @PutMapping("/assignments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Assignment updateAssignment(@PathVariable Long id, @Valid @RequestBody AssignmentRequest request) {
        Assignment assignment = assignmentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        assignment.setClassEntity(classEntityRepository.findById(request.getClassId()).orElseThrow(() -> new ResourceNotFoundException("Class not found")));
        assignment.setTeacher(teacherRepository.findById(request.getTeacherId()).orElseThrow(() -> new ResourceNotFoundException("Teacher not found")));
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setFilePath(request.getFilePath());
        Assignment saved = assignmentRepository.save(assignment);
        log("UPDATE", "Assignment", String.valueOf(saved.getId()), saved.getTitle());
        return saved;
    }

    @DeleteMapping("/assignments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        assignmentRepository.deleteById(id);
        log("DELETE", "Assignment", String.valueOf(id), "Removed assignment");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/submissions")
    public List<Submission> submissions() {
        com.education.erp.security.services.UserDetailsImpl principal = currentPrincipal();
        if (principal != null && hasAuthority(principal, "ROLE_STUDENT")) {
            Student student = currentStudent(principal);
            return submissionRepository.findByStudent_IdOrderBySubmittedAtDesc(student.getId());
        }
        return submissionRepository.findAll();
    }

    @PostMapping("/submissions")
    public Submission createSubmission(@Valid @RequestBody SubmissionRequest request) {
        Submission submission = new Submission();
        submission.setAssignment(assignmentRepository.findById(request.getAssignmentId()).orElseThrow(() -> new ResourceNotFoundException("Assignment not found")));
        com.education.erp.security.services.UserDetailsImpl principal = currentPrincipal();
        if (principal != null && hasAuthority(principal, "ROLE_STUDENT")) {
            submission.setStudent(currentStudent(principal));
        } else {
            submission.setStudent(studentRepository.findById(request.getStudentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found")));
        }
        submission.setFilePath(request.getFilePath());
        submission.setRemarks(request.getRemarks());
        submission.setStatus(request.getStatus() != null ? request.getStatus() : "SUBMITTED");
        Submission saved = submissionRepository.save(submission);
        log("CREATE", "Submission", String.valueOf(saved.getId()), saved.getStatus());
        return saved;
    }

    @PostMapping(value = "/submissions/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public Submission uploadSubmission(
            @org.springframework.web.bind.annotation.RequestParam Long assignmentId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String remarks,
            @org.springframework.web.bind.annotation.RequestParam(required = false) MultipartFile file) throws IOException {
        com.education.erp.security.services.UserDetailsImpl principal = currentPrincipal();
        Student student = currentStudent(principal);
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        Submission submission = submissionRepository.findByAssignment_IdAndStudent_Id(assignmentId, student.getId())
                .orElseGet(Submission::new);
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setRemarks(remarks);
        submission.setStatus("SUBMITTED");

        if (file != null && !file.isEmpty()) {
            String originalName = file.getOriginalFilename() == null ? "assignment.pdf" : file.getOriginalFilename();
            String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            Path uploadDir = Path.of(System.getProperty("user.dir"), "uploads", "submissions", String.valueOf(student.getId()));
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(UUID.randomUUID() + "-" + safeName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            submission.setFilePath(target.toString());
        }

        Submission saved = submissionRepository.save(submission);
        log("CREATE", "Submission", String.valueOf(saved.getId()), saved.getStatus());
        return saved;
    }

    @PutMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Submission updateSubmission(@PathVariable Long id, @Valid @RequestBody SubmissionRequest request) {
        Submission submission = submissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
        submission.setAssignment(assignmentRepository.findById(request.getAssignmentId()).orElseThrow(() -> new ResourceNotFoundException("Assignment not found")));
        submission.setStudent(studentRepository.findById(request.getStudentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found")));
        submission.setFilePath(request.getFilePath());
        submission.setRemarks(request.getRemarks());
        submission.setStatus(request.getStatus());
        Submission saved = submissionRepository.save(submission);
        log("UPDATE", "Submission", String.valueOf(saved.getId()), saved.getStatus());
        return saved;
    }

    @DeleteMapping("/submissions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        submissionRepository.deleteById(id);
        log("DELETE", "Submission", String.valueOf(id), "Removed submission");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/messages")
    public List<Message> messages() {
        com.education.erp.security.services.UserDetailsImpl principal = currentPrincipal();
        if (principal != null) {
            return messageRepository.findBySender_IdOrRecipient_IdOrderBySentAtDesc(principal.getId(), principal.getId());
        }
        return messageRepository.findAll();
    }

    @GetMapping("/message-recipients")
    public List<User> messageRecipients() {
        com.education.erp.security.services.UserDetailsImpl principal = currentPrincipal();
        if (principal != null && hasAuthority(principal, "ROLE_STUDENT")) {
            return userRepository.findByRole_NameIn(List.of("ROLE_TEACHER", "ROLE_ADMIN"));
        }
        return userRepository.findAll();
    }

    @PostMapping("/messages")
    public Message createMessage(@Valid @RequestBody MessageRequest request) {
        com.education.erp.security.services.UserDetailsImpl principal = currentPrincipal();
        Message message = new Message();
        message.setSender(principal != null
                ? userRepository.findById(principal.getId()).orElseThrow(() -> new ResourceNotFoundException("Sender not found"))
                : userRepository.findById(request.getSenderId()).orElseThrow(() -> new ResourceNotFoundException("Sender not found")));
        User recipient = userRepository.findById(request.getRecipientId()).orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));
        if (principal != null && hasAuthority(principal, "ROLE_STUDENT")
                && "ROLE_STUDENT".equals(recipient.getRole().getName())) {
            throw new ResourceNotFoundException("Students can message only teachers or admins");
        }
        message.setRecipient(recipient);
        message.setSubject(request.getSubject());
        message.setBody(request.getBody());
        Message saved = messageRepository.save(message);
        log("CREATE", "Message", String.valueOf(saved.getId()), saved.getSubject());
        return saved;
    }

    @GetMapping("/notifications")
    public List<Notification> notifications() {
        com.education.erp.security.services.UserDetailsImpl principal = currentPrincipal();
        if (principal != null && hasAuthority(principal, "ROLE_STUDENT")) {
            return notificationRepository.findByActiveTrueAndAudienceRoleInOrderByCreatedAtDesc(List.of("ALL", "ROLE_STUDENT", "STUDENT"));
        }
        return notificationRepository.findAll();
    }

    @PostMapping("/notifications")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Notification createNotification(@Valid @RequestBody NotificationRequest request) {
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setMessageBody(request.getMessageBody());
        notification.setAudienceRole(request.getAudienceRole());
        if (request.getActive() != null) {
            notification.setActive(request.getActive());
        }
        Notification saved = notificationRepository.save(notification);
        log("CREATE", "Notification", String.valueOf(saved.getId()), saved.getTitle());
        return saved;
    }

    @PutMapping("/notifications/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Notification updateNotification(@PathVariable Long id, @Valid @RequestBody NotificationRequest request) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setTitle(request.getTitle());
        notification.setMessageBody(request.getMessageBody());
        notification.setAudienceRole(request.getAudienceRole());
        if (request.getActive() != null) {
            notification.setActive(request.getActive());
        }
        Notification saved = notificationRepository.save(notification);
        log("UPDATE", "Notification", String.valueOf(saved.getId()), saved.getTitle());
        return saved;
    }

    @DeleteMapping("/notifications/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationRepository.deleteById(id);
        log("DELETE", "Notification", String.valueOf(id), "Removed notification");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logs")
    public List<com.education.erp.model.ActivityLog> logs() {
        return activityLogRepository.findTop10ByOrderByCreatedAtDesc();
    }

    private User createAccount(String name, String email, String password, String roleName) {
        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResourceNotFoundException("User already exists");
        }
        Role role = roleRepository.findByNameIgnoreCase(roleName).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        User user = new User();
        user.setFullName(name.trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    private void log(String action, String entityType, String entityId, String details) {
        String actorEmail = currentActorEmail();
        activityLogService.record(actorEmail, action, entityType, entityId, details);
    }

    private String currentActorEmail() {
        com.education.erp.security.services.UserDetailsImpl principal = currentPrincipal();
        return principal != null ? principal.getEmail() : null;
    }

    private com.education.erp.security.services.UserDetailsImpl currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof com.education.erp.security.services.UserDetailsImpl principal) {
            return principal;
        }
        return null;
    }

    private boolean hasAuthority(com.education.erp.security.services.UserDetailsImpl principal, String authority) {
        return principal.getAuthorities().stream()
                .anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
    }

    private Student currentStudent(com.education.erp.security.services.UserDetailsImpl principal) {
        if (principal == null) {
            throw new ResourceNotFoundException("Student not found");
        }
        return studentRepository.findByUser_Id(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }

    private boolean assignmentMatchesStudent(Assignment assignment, Student student) {
        if (assignment.getClassEntity() == null || student.getClassName() == null) {
            return false;
        }
        String studentClass = student.getClassName().toLowerCase(Locale.ROOT);
        String className = assignment.getClassEntity().getName() == null ? "" : assignment.getClassEntity().getName().toLowerCase(Locale.ROOT);
        String classCode = assignment.getClassEntity().getClassCode() == null ? "" : assignment.getClassEntity().getClassCode().toLowerCase(Locale.ROOT);
        return studentClass.equals(className) || studentClass.equals(classCode) || className.contains(studentClass);
    }
}
