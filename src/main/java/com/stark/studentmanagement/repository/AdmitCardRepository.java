package com.stark.studentmanagement.repository;

import com.stark.studentmanagement.entity.AdmitCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdmitCardRepository extends JpaRepository<AdmitCard, Long> {
    
    Optional<AdmitCard> findByStudentId(Long studentId);
}
