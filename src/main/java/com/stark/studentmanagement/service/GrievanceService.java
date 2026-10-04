package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.Grievance;
import com.stark.studentmanagement.entity.User;
import com.stark.studentmanagement.repository.GrievanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GrievanceService {
    
    @Autowired
    private GrievanceRepository grievanceRepository;
    
    public List<Grievance> getByStudentId(Long studentId) {
        return grievanceRepository.findByStudentId(studentId);
    }
    
    public List<Grievance> getByStatus(Grievance.GrievanceStatus status) {
        return grievanceRepository.findByStatus(status);
    }
    
    public Grievance submitGrievance(Long studentId, String category, String description) {
        Grievance grievance = new Grievance();
        User student = new User();
        student.setId(studentId);
        grievance.setStudent(student);
        grievance.setCategory(category);
        grievance.setDescription(description);
        grievance.setStatus(Grievance.GrievanceStatus.PENDING);
        return grievanceRepository.save(grievance);
    }
    
    public Grievance updateStatus(Long id, Grievance.GrievanceStatus status) {
        Grievance grievance = grievanceRepository.findById(id).orElse(null);
        if (grievance != null) {
            grievance.setStatus(status);
            return grievanceRepository.save(grievance);
        }
        return null;
    }
}
