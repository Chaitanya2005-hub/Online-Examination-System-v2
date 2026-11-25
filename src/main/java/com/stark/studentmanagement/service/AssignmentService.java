package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.Assignment;
import com.stark.studentmanagement.entity.Submission;
import com.stark.studentmanagement.repository.AssignmentRepository;
import com.stark.studentmanagement.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssignmentService {
    
    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;
    
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }
    
    public List<Assignment> getAssignmentsByTeacher(Long teacherId) {
        return assignmentRepository.findByCreatedById(teacherId);
    }

    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id).orElse(null);
    }

    public List<Submission> getSubmissionsByAssignmentId(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }
    
    public Assignment saveAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }
    
    @Transactional
    public void deleteAssignment(Long id) {
        List<Submission> submissions = submissionRepository.findByAssignmentId(id);
        if (!submissions.isEmpty()) {
            submissionRepository.deleteAll(submissions);
        }
        assignmentRepository.deleteById(id);
    }
}
