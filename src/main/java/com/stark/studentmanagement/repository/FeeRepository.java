package com.stark.studentmanagement.repository;

import com.stark.studentmanagement.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {
    
    Optional<Fee> findByStudentId(Long studentId);
    
    List<Fee> findByStatus(Fee.FeeStatus status);
}
