package com.education.erp.service;

import com.education.erp.exception.BadRequestException;
import com.education.erp.payload.response.CaptchaChallengeResponse;
import com.education.erp.payload.response.CaptchaImageResponse;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CaptchaService {

    private static final String QUESTION = "Select all images with traffic lights";
    private static final Duration EXPIRY = Duration.ofMinutes(4);
    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final Map<String, CaptchaChallenge> challenges = new ConcurrentHashMap<>();

    public CaptchaChallengeResponse createChallenge() {
        cleanupExpiredChallenges();

        String sessionId = UUID.randomUUID().toString();
        List<CaptchaOption> options = buildOptions();
        Set<String> correctIds = options.stream()
                .filter(CaptchaOption::isCorrect)
                .map(CaptchaOption::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        challenges.put(sessionId, new CaptchaChallenge(sessionId, QUESTION, options, correctIds, Instant.now().plus(EXPIRY)));

        List<CaptchaImageResponse> images = options.stream()
                .map(option -> new CaptchaImageResponse(option.getId(), "/api/auth/captcha-images/" + sessionId + "/" + option.getId()))
                .toList();

        return new CaptchaChallengeResponse(sessionId, QUESTION, images);
    }

    public String renderImage(String sessionId, String imageId) {
        CaptchaChallenge challenge = getValidChallenge(sessionId);
        CaptchaOption option = challenge.getOptions().stream()
                .filter(item -> item.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Captcha image not found"));

        return buildSvg(option.getLabel());
    }

    public boolean verifyChallenge(String sessionId, List<String> selectedImageIds) {
        CaptchaChallenge challenge = getValidChallenge(sessionId);
        Set<String> selected = selectedImageIds == null ? Collections.emptySet() : new LinkedHashSet<>(selectedImageIds);

        if (!selected.equals(challenge.getCorrectIds())) {
            challenge.incrementFailedAttempts();
            if (challenge.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
                challenges.remove(sessionId);
            }
            return false;
        }

        challenges.remove(sessionId);
        return true;
    }

    private CaptchaChallenge getValidChallenge(String sessionId) {
        cleanupExpiredChallenges();
        CaptchaChallenge challenge = challenges.get(sessionId);
        if (challenge == null) {
            throw new BadRequestException("Captcha session expired. Please refresh and try again.");
        }
        return challenge;
    }

    private void cleanupExpiredChallenges() {
        Instant now = Instant.now();
        challenges.entrySet().removeIf(entry -> entry.getValue().getExpiresAt().isBefore(now));
    }

    private List<CaptchaOption> buildOptions() {
        List<String> labels = new ArrayList<>(List.of(
                "traffic_light", "traffic_light", "traffic_light",
                "car", "bus", "bicycle", "tree", "dog", "school"
        ));
        Collections.shuffle(labels);

        List<CaptchaOption> options = new ArrayList<>();
        for (String label : labels) {
            options.add(new CaptchaOption(UUID.randomUUID().toString(), label, "traffic_light".equals(label)));
        }
        return options;
    }

    private String buildSvg(String label) {
        Map<String, String> svgMap = new HashMap<>();
        svgMap.put("traffic_light", "<rect x='36' y='14' width='52' height='72' rx='14' fill='#111827'/><circle cx='62' cy='30' r='10' fill='#ef4444'/><circle cx='62' cy='50' r='10' fill='#f59e0b'/><circle cx='62' cy='70' r='10' fill='#22c55e'/><rect x='58' y='86' width='8' height='38' rx='4' fill='#6b7280'/>");
        svgMap.put("car", "<rect x='20' y='54' width='72' height='20' rx='8' fill='#2563eb'/><path d='M34 54 L44 38 H68 L78 54 Z' fill='#60a5fa'/><circle cx='36' cy='78' r='8' fill='#111827'/><circle cx='76' cy='78' r='8' fill='#111827'/>");
        svgMap.put("bus", "<rect x='18' y='28' width='76' height='46' rx='10' fill='#f59e0b'/><rect x='26' y='36' width='14' height='12' fill='#fff7ed'/><rect x='44' y='36' width='14' height='12' fill='#fff7ed'/><rect x='62' y='36' width='14' height='12' fill='#fff7ed'/><circle cx='32' cy='78' r='7' fill='#111827'/><circle cx='78' cy='78' r='7' fill='#111827'/>");
        svgMap.put("bicycle", "<circle cx='30' cy='72' r='14' fill='none' stroke='#2563eb' stroke-width='6'/><circle cx='82' cy='72' r='14' fill='none' stroke='#2563eb' stroke-width='6'/><path d='M30 72 L50 40 L66 72 Z' fill='none' stroke='#111827' stroke-width='5' stroke-linecap='round' stroke-linejoin='round'/><path d='M50 40 L72 40' fill='none' stroke='#111827' stroke-width='5' stroke-linecap='round'/>");
        svgMap.put("tree", "<rect x='54' y='62' width='12' height='34' rx='4' fill='#92400e'/><circle cx='60' cy='42' r='24' fill='#22c55e'/><circle cx='42' cy='48' r='16' fill='#16a34a'/><circle cx='78' cy='48' r='16' fill='#16a34a'/>");
        svgMap.put("dog", "<circle cx='36' cy='42' r='12' fill='#d97706'/><circle cx='84' cy='42' r='12' fill='#d97706'/><path d='M30 50 H82 V78 H30 Z' fill='#f59e0b'/><circle cx='46' cy='62' r='4' fill='#111827'/><circle cx='66' cy='62' r='4' fill='#111827'/><path d='M52 70 Q56 76 60 70' fill='none' stroke='#111827' stroke-width='3' stroke-linecap='round'/>");
        svgMap.put("school", "<rect x='24' y='34' width='72' height='56' rx='6' fill='#475569'/><polygon points='60,18 26,36 94,36' fill='#f97316'/><rect x='54' y='54' width='12' height='36' fill='#e2e8f0'/><rect x='34' y='48' width='12' height='12' fill='#e2e8f0'/><rect x='74' y='48' width='12' height='12' fill='#e2e8f0'/>");

        String body = svgMap.getOrDefault(label, svgMap.get("traffic_light"));
        return "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 120 120' width='120' height='120'>" +
                "<rect width='120' height='120' rx='20' fill='#f8fafc'/><rect x='6' y='6' width='108' height='108' rx='16' fill='white' stroke='#cbd5e1'/>" +
                body +
                "</svg>";
    }

    private static class CaptchaChallenge {
        @Getter
        private final String sessionId;
        @Getter
        private final String question;
        @Getter
        private final List<CaptchaOption> options;
        @Getter
        private final Set<String> correctIds;
        @Getter
        private final Instant expiresAt;
        @Getter
        private int failedAttempts;

        private CaptchaChallenge(String sessionId, String question, List<CaptchaOption> options, Set<String> correctIds, Instant expiresAt) {
            this.sessionId = sessionId;
            this.question = question;
            this.options = options;
            this.correctIds = correctIds;
            this.expiresAt = expiresAt;
        }

        private void incrementFailedAttempts() {
            this.failedAttempts++;
        }
    }

    private record CaptchaOption(String id, String label, boolean correct) {
        private String getId() {
            return id;
        }

        private String getLabel() {
            return label;
        }

        private boolean isCorrect() {
            return correct;
        }
    }
}