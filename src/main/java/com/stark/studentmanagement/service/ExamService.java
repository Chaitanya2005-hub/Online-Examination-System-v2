package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.Exam;
import com.stark.studentmanagement.entity.Question;
import com.stark.studentmanagement.entity.Result;
import com.stark.studentmanagement.entity.User;
import com.stark.studentmanagement.repository.ExamRepository;
import com.stark.studentmanagement.repository.QuestionRepository;
import com.stark.studentmanagement.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService {
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private ResultRepository resultRepository;
    
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }
    
    public List<Exam> getAvailableExams() {
        return examRepository.findByStatus(Exam.ExamStatus.SCHEDULED);
    }
    
    public Exam findById(Long id) {
        return examRepository.findById(id).orElse(null);
    }
    
    public Exam saveExam(Exam exam) {
        return examRepository.save(exam);
    }
    
    public void deleteExam(Long id) {
        questionRepository.deleteByExamId(id);
        examRepository.deleteById(id);
    }
    
    public void submitExam(Long studentId, Long examId, Long[] answers) {
        List<Question> questions = questionRepository.findByExamId(examId);
        int score = 0;
        int totalMarks = questions.size();
        
        if (answers != null) {
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                if (i < answers.length && answers[i] != null) {
                    // Convert answer index to letter (0=A, 1=B, 2=C, 3=D)
                    String answerLetter = String.valueOf((char) ('A' + answers[i]));
                    if (answerLetter.equals(question.getCorrectAnswer())) {
                        score++;
                    }
                }
            }
        }
        
        Result result = new Result();
        User student = new User();
        student.setId(studentId);
        result.setStudent(student);
        Exam exam = new Exam();
        exam.setId(examId);
        result.setExam(exam);
        result.setScore(score);
        result.setTotalMarks(totalMarks);
        result.setStatus("Completed");
        result.setSecurityWarnings(0);

        resultRepository.save(result);
    }
}
