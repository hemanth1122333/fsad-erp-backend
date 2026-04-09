package com.education.erp.service;

import com.education.erp.model.ActivityLog;
import com.education.erp.model.User;
import com.education.erp.repository.ActivityLogRepository;
import com.education.erp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public void record(String actorEmail, String action, String entityType, String entityId, String details) {
        ActivityLog log = new ActivityLog();
        if (actorEmail != null && !actorEmail.isBlank()) {
            User user = userRepository.findByEmailIgnoreCase(actorEmail).orElse(null);
            log.setActor(user);
        }
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        activityLogRepository.save(log);
    }
}