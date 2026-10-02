package com.stark.studentmanagement.controller;

import com.stark.studentmanagement.entity.User;
import com.stark.studentmanagement.service.ProctoringService;
import com.stark.studentmanagement.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proctor")
@RequiredArgsConstructor
public class ProctoringApiController {

    private final ProctoringService proctoringService;
    private final UserService userService;

    @Data
    public static class FramePayload {
        private Long examId;
        private String examTitle;
        private String frameBase64;
        private int warningCount;
        private String violationType;
    }

    @PostMapping("/stream")
    public ResponseEntity<String> receiveFrame(@RequestBody FramePayload payload, Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : null;
        User student = (username != null) ? userService.findByUsername(username) : null;

        Long studentId = (student != null) ? student.getId() : 1L;
        String studentName = (student != null) ? student.getFullName() : "Active Student";
        String erpId = (student != null && student.getErpId() != null) ? student.getErpId() : (student != null ? student.getUsername() : "STD-LIVE");

        proctoringService.updateSession(
                studentId,
                studentName,
                erpId,
                payload.getExamId(),
                payload.getExamTitle(),
                payload.getFrameBase64(),
                payload.getWarningCount(),
                payload.getViolationType()
        );

        return ResponseEntity.ok("Frame Received");
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ProctoringService.LiveProctorSession>> getActiveSessions() {
        return ResponseEntity.ok(proctoringService.getActiveSessions());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<ProctoringService.ViolationLog>> getViolationLogs() {
        return ResponseEntity.ok(proctoringService.getViolationLogs());
    }
}
