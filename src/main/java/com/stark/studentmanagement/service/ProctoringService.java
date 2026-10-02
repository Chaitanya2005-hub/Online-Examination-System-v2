package com.stark.studentmanagement.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProctoringService {

    private final Map<Long, LiveProctorSession> activeSessions = new ConcurrentHashMap<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LiveProctorSession {
        private Long studentId;
        private String studentName;
        private String erpId;
        private Long examId;
        private String examTitle;
        private String frameBase64;
        private int warningCount;
        private LocalDateTime lastUpdated;
        private boolean active;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViolationLog {
        private String id;
        private Long studentId;
        private String studentName;
        private String erpId;
        private String examTitle;
        private String violationType;
        private String frameBase64;
        private LocalDateTime timestamp;
    }

    private final List<ViolationLog> violationLogs = new java.util.concurrent.CopyOnWriteArrayList<>();

    public void updateSession(Long studentId, String studentName, String erpId, Long examId, String examTitle, String frameBase64, int warningCount, String violationType) {
        LiveProctorSession session = activeSessions.getOrDefault(studentId, new LiveProctorSession());
        session.setStudentId(studentId);
        session.setStudentName(studentName);
        session.setErpId(erpId);
        session.setExamId(examId);
        session.setExamTitle(examTitle);
        if (frameBase64 != null && !frameBase64.isEmpty()) {
            session.setFrameBase64(frameBase64);
        }
        session.setWarningCount(warningCount);
        session.setLastUpdated(LocalDateTime.now());
        session.setActive(true);
        activeSessions.put(studentId, session);

        // Record violation log evidence if flagged
        if (violationType != null && !violationType.isEmpty() && warningCount > 0) {
            ViolationLog log = new ViolationLog(
                    java.util.UUID.randomUUID().toString(),
                    studentId,
                    studentName,
                    erpId,
                    examTitle,
                    violationType,
                    frameBase64,
                    LocalDateTime.now()
            );
            violationLogs.add(0, log); // Add newest first
            if (violationLogs.size() > 100) {
                violationLogs.remove(violationLogs.size() - 1);
            }
        }
    }

    public List<LiveProctorSession> getActiveSessions() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(2);
        List<LiveProctorSession> result = new ArrayList<>();
        activeSessions.values().forEach(session -> {
            if (session.getLastUpdated() != null && session.getLastUpdated().isAfter(threshold)) {
                result.add(session);
            }
        });
        return result;
    }

    public List<ViolationLog> getViolationLogs() {
        return new ArrayList<>(violationLogs);
    }

    public LiveProctorSession getSessionByStudentId(Long studentId) {
        return activeSessions.get(studentId);
    }

    public void endSession(Long studentId) {
        activeSessions.remove(studentId);
    }
}
