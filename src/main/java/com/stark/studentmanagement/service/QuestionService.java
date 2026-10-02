package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.Question;
import com.stark.studentmanagement.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    public List<Question> getQuestionsByExamId(Long examId) {
        if (examId == null) {
            return questionRepository.findAll();
        }
        return questionRepository.findByExamId(examId);
    }
    
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }
    
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
