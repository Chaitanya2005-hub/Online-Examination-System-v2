package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.Result;
import com.stark.studentmanagement.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {
    
    @Autowired
    private ResultRepository resultRepository;
    
    public List<Result> getResultsByStudentId(Long studentId) {
        return resultRepository.findByStudentId(studentId);
    }
    
    public List<Result> getResultsByExamId(Long examId) {
        return resultRepository.findByExamId(examId);
    }
}
