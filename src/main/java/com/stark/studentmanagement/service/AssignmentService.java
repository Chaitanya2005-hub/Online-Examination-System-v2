package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.Assignment;
import com.stark.studentmanagement.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentService {
    
    @Autowired
    private AssignmentRepository assignmentRepository;
    
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
    
    public List<Assignment> getAssignmentsByTeacher(Long teacherId) {
        return assignmentRepository.findByCreatedById(teacherId);
    }
    
    public Assignment saveAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }
    
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }
}
