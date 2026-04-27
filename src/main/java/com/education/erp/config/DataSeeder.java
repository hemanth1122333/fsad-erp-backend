package com.education.erp.config;

import com.education.erp.model.*;
import com.education.erp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private ClassEntityRepository classEntityRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private GradeRepository gradeRepository;
    @Autowired private AssignmentRepository assignmentRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();

        // Seed core users
        User admin = seedUser("Priya Admin", "admin@erp.com", "123", "ROLE_ADMIN");
        Teacher teacher1 = seedTeacher("Mr. Rajesh Kumar", "teacher@erp.com", "123", "TCH-1001", "Computer Science");
        Teacher teacher2 = seedTeacher("Mrs. Ananya Sharma", "teacher2@erp.com", "123", "TCH-1002", "Mathematics");
        Student student1 = seedStudent("Arjun Singh", "student@erp.com", "123", "STD-2401", "10-A", 2024);
        Student student2 = seedStudent("Priya Patel", "student2@erp.com", "123", "STD-2402", "10-A", 2024);
        Student student3 = seedStudent("Vikram Desai", "student3@erp.com", "123", "STD-2403", "10-A", 2024);

        // Seed classes
        ClassEntity class1 = seedClass("CS-101", "Data Structures", "Introduction to DSA", "Monday", "09:00", "10:30", "Lab-1", teacher1);
        ClassEntity class2 = seedClass("MATH-201", "Calculus", "Advanced Mathematics", "Tuesday", "11:00", "12:30", "Room-5", teacher2);

        // Seed attendance
        seedAttendance(student1, class1);
        seedAttendance(student2, class1);
        seedAttendance(student3, class1);

        // Seed grades
        seedGrades(student1, class1);
        seedGrades(student2, class1);
        seedGrades(student3, class1);

        // Seed assignments
        seedAssignments(class1, teacher1);

        // Seed notifications
        seedNotifications();

        System.out.println("\n✓ ERP System seeded with realistic data!");
        System.out.println("Demo Accounts:");
        System.out.println("  Admin: admin@erp.com / 123");
        System.out.println("  Teacher: teacher@erp.com / 123");
        System.out.println("  Student: student@erp.com / 123\n");
    }


    private void seedRoles() {
        List.of("ROLE_ADMIN", "ROLE_TEACHER", "ROLE_STUDENT")
                .forEach(roleName -> {
                    if (roleRepository.findByNameIgnoreCase(roleName).isEmpty()) {
                        roleRepository.save(new Role(roleName));
                    }
                });
    }

    private User seedUser(String fullName, String email, String password, String roleName) {
        return userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            Role role = roleRepository.findByNameIgnoreCase(roleName).orElseThrow();
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            user.setCreatedAt(LocalDateTime.now());
            return userRepository.save(user);
        });
    }

    private Teacher seedTeacher(String fullName, String email, String password, String employeeCode, String department) {
        User user = seedUser(fullName, email, password, "ROLE_TEACHER");
        return teacherRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setEmployeeCode(employeeCode);
            teacher.setDepartment(department);
            teacher.setPhone("9876543210");
            teacher.setOfficeRoom("A-" + (100 + (int)(Math.random() * 99)));
            return teacherRepository.save(teacher);
        });
    }

    private Student seedStudent(String fullName, String email, String password, String admissionNumber, String className, int batchYear) {
        User user = seedUser(fullName, email, password, "ROLE_STUDENT");
        return studentRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            Student student = new Student();
            student.setUser(user);
            student.setAdmissionNumber(admissionNumber);
            student.setClassName(className);
            student.setBatchYear(batchYear);
            student.setPhone("98765" + (10000 + (int)(Math.random() * 89999)));
            student.setGuardianName("Parent / Guardian");
            student.setAddress("City, State 000001");
            return studentRepository.save(student);
        });
    }

    private ClassEntity seedClass(String code, String name, String desc, String day, String start, String end, String room, Teacher teacher) {
        if (classEntityRepository.findByClassCode(code).isEmpty()) {
            ClassEntity classEntity = new ClassEntity();
            classEntity.setClassCode(code);
            classEntity.setName(name);
            classEntity.setDescription(desc);
            classEntity.setScheduleDay(day);
            classEntity.setStartTime(start);
            classEntity.setEndTime(end);
            classEntity.setRoom(room);
            classEntity.setTeacher(teacher);
            classEntity.setCreatedAt(LocalDateTime.now());
            return classEntityRepository.save(classEntity);
        }
        return classEntityRepository.findByClassCode(code).orElseThrow();
    }

    private void seedAttendance(Student student, ClassEntity classEntity) {
        for (int i = 0; i < 15; i++) {
            if (attendanceRepository.findByStudentIdAndClassIdAndAttendanceDate(student.getId(), classEntity.getId(), LocalDate.now().minusDays(i)).isEmpty()) {
                Attendance attendance = new Attendance();
                attendance.setStudent(student);
                attendance.setClassEntity(classEntity);
                attendance.setAttendanceDate(LocalDate.now().minusDays(i));
                attendance.setStatus(i % 3 == 0 ? "ABSENT" : "PRESENT");
                attendance.setRemarks(i % 3 == 0 ? "Sick leave" : "");
                attendanceRepository.save(attendance);
            }
        }
    }

    private void seedGrades(Student student, ClassEntity classEntity) {
        List<GradeSeed> grades = gradeSeedsFor(student.getAdmissionNumber());
        normalizeExistingGrades(student, classEntity, grades);

        for (GradeSeed seed : grades) {
            Grade grade = gradeRepository.findByStudent_IdAndSubjectAndAssessmentName(student.getId(), seed.subject(), seed.assessment())
                    .orElseGet(Grade::new);
            grade.setStudent(student);
            grade.setClassEntity(classEntity);
            grade.setAssessmentName(seed.assessment());
            grade.setSubject(seed.subject());
            grade.setMarksObtained(BigDecimal.valueOf(seed.marks()));
            grade.setMaxMarks(BigDecimal.valueOf(100));
            grade.setGradeLetter(gradeLetter(seed.marks()));
            grade.setRemark(remarkFor(seed.marks()));
            grade.setRecordedAt(LocalDateTime.now());
            gradeRepository.save(grade);
        }
    }

    private void normalizeExistingGrades(Student student, ClassEntity classEntity, List<GradeSeed> seeds) {
        List<Grade> existingGrades = gradeRepository.findByStudent_Id(student.getId()).stream()
                .filter(grade -> grade.getClassEntity() != null && grade.getClassEntity().getId().equals(classEntity.getId()))
                .sorted(Comparator.comparing(Grade::getId))
                .toList();

        int updateCount = Math.min(existingGrades.size(), seeds.size());
        for (int i = 0; i < updateCount; i++) {
            Grade grade = existingGrades.get(i);
            GradeSeed seed = seeds.get(i);
            grade.setAssessmentName(seed.assessment());
            grade.setSubject(seed.subject());
            grade.setMarksObtained(BigDecimal.valueOf(seed.marks()));
            grade.setMaxMarks(BigDecimal.valueOf(100));
            grade.setGradeLetter(gradeLetter(seed.marks()));
            grade.setRemark(remarkFor(seed.marks()));
            gradeRepository.save(grade);
        }
    }

    private List<GradeSeed> gradeSeedsFor(String admissionNumber) {
        if ("STD-2402".equals(admissionNumber)) {
            return List.of(
                    new GradeSeed("Data Structures", "Quiz 1", 78),
                    new GradeSeed("Database Systems", "Mid Term", 86),
                    new GradeSeed("Operating Systems", "Lab Practical", 64),
                    new GradeSeed("Web Technology", "Project", 91),
                    new GradeSeed("Discrete Mathematics", "Assignment", 58),
                    new GradeSeed("Technical Communication", "Presentation", 73)
            );
        }
        if ("STD-2403".equals(admissionNumber)) {
            return List.of(
                    new GradeSeed("Data Structures", "Quiz 1", 42),
                    new GradeSeed("Database Systems", "Mid Term", 51),
                    new GradeSeed("Operating Systems", "Lab Practical", 36),
                    new GradeSeed("Web Technology", "Project", 67),
                    new GradeSeed("Discrete Mathematics", "Assignment", 28),
                    new GradeSeed("Technical Communication", "Presentation", 61)
            );
        }
        return List.of(
                new GradeSeed("Data Structures", "Quiz 1", 88),
                new GradeSeed("Database Systems", "Mid Term", 76),
                new GradeSeed("Operating Systems", "Lab Practical", 69),
                new GradeSeed("Web Technology", "Project", 93),
                new GradeSeed("Discrete Mathematics", "Assignment", 54),
                new GradeSeed("Technical Communication", "Presentation", 82)
        );
    }

    private String gradeLetter(double marks) {
        if (marks >= 85) {
            return "A";
        }
        if (marks >= 70) {
            return "B";
        }
        if (marks >= 55) {
            return "C";
        }
        if (marks >= 40) {
            return "D";
        }
        return "F";
    }

    private String remarkFor(double marks) {
        if (marks >= 85) {
            return "Excellent work. Keep the same consistency.";
        }
        if (marks >= 70) {
            return "Good progress. Strengthen revision before exams.";
        }
        if (marks >= 55) {
            return "Satisfactory. Needs more practice for higher accuracy.";
        }
        if (marks >= 40) {
            return "Needs improvement. Revise basics and submit practice work.";
        }
        return "Poor performance. Meet the teacher for a recovery plan.";
    }

    private record GradeSeed(String subject, String assessment, double marks) {}

    private void seedAssignments(ClassEntity classEntity, Teacher teacher) {
        String[] assignments = {"DSA Problem Set 1", "Recursion Exercises", "Graph Algorithms", "Dynamic Programming"};
        for (String title : assignments) {
            if (assignmentRepository.findByTitle(title).isEmpty()) {
                Assignment assignment = new Assignment();
                assignment.setTitle(title);
                assignment.setDescription("Complete all 10 problems and submit code");
                assignment.setClassEntity(classEntity);
                assignment.setTeacher(teacher);
                assignment.setDueDate(LocalDateTime.now().plusDays(7));
                assignment.setCreatedAt(LocalDateTime.now());
                assignmentRepository.save(assignment);
            }
        }
    }

    private void seedNotifications() {
        if (notificationRepository.count() < 3) {
            String[] titles = {"System Maintenance", "Assignment Deadline", "Class Cancelled"};
            String[] messages = {
                "Scheduled maintenance on Sunday 2 AM - 4 AM IST",
                "Submit your assignments by Friday EOD",
                "Class CS-101 rescheduled to Thursday"
            };
            for (int i = 0; i < titles.length; i++) {
                Notification notif = new Notification();
                notif.setTitle(titles[i]);
                notif.setMessageBody(messages[i]);
                notif.setAudienceRole("ALL");
                notif.setActive(true);
                notif.setCreatedAt(LocalDateTime.now());
                notificationRepository.save(notif);
            }
        }
    }
}
