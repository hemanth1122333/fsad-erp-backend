package com.education.erp.service;

import com.education.erp.payload.response.DashboardSummaryResponse;
import com.education.erp.repository.ActivityLogRepository;
import com.education.erp.repository.AssignmentRepository;
import com.education.erp.repository.ClassEntityRepository;
import com.education.erp.repository.NotificationRepository;
import com.education.erp.repository.StudentRepository;
import com.education.erp.repository.SubmissionRepository;
import com.education.erp.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ClassEntityRepository classEntityRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final NotificationRepository notificationRepository;
    private final ActivityLogRepository activityLogRepository;

    public DashboardSummaryResponse getSummary() {
        List<String> recentLogs = activityLogRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(log -> log.getAction() + " - " + (log.getDetails() == null ? "" : log.getDetails()))
                .toList();

        return new DashboardSummaryResponse(
                studentRepository.count(),
                teacherRepository.count(),
                classEntityRepository.count(),
                assignmentRepository.count(),
                submissionRepository.count(),
                notificationRepository.count(),
                recentLogs
        );
    }
}