package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.LiveCode;
import com.stark.studentmanagement.repository.LiveCodeRepository;
import com.stark.studentmanagement.util.QrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LiveCodeService {
    
    @Autowired
    private LiveCodeRepository liveCodeRepository;
    
    @Autowired
    private QrService qrService;
    
    public String generateLiveCode() {
        // Delete expired codes
        liveCodeRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        
        String code = qrService.generateRandomCode(4);
        LiveCode liveCode = new LiveCode();
        liveCode.setCode(code);
        liveCode.setCreatedAt(LocalDateTime.now());
        liveCode.setExpiresAt(LocalDateTime.now().plusSeconds(10));
        
        liveCodeRepository.save(liveCode);
        return code;
    }
    
    public Optional<LiveCode> getCurrentCode() {
        return liveCodeRepository.findByCode(generateLiveCode());
    }
    
    @Scheduled(fixedRate = 10000)
    public void refreshCode() {
        generateLiveCode();
    }
}
