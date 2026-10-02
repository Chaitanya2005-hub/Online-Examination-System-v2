package com.stark.studentmanagement.repository;

import com.stark.studentmanagement.entity.LiveCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LiveCodeRepository extends JpaRepository<LiveCode, String> {
    
    Optional<LiveCode> findByCode(String code);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
