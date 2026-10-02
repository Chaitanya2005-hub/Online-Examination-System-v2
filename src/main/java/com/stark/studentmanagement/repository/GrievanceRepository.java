package com.stark.studentmanagement.repository;

import com.stark.studentmanagement.entity.Grievance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrievanceRepository extends JpaRepository<Grievance, Long> {
    
    List<Grievance> findByStudentId(Long studentId);
    
    List<Grievance> findByStatus(Grievance.GrievanceStatus status);
}
