package com.stark.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "exam_date")
    private LocalDate examDate;
    
    @Column(name = "start_time")
    private LocalTime startTime;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Enumerated(EnumType.STRING)
    private ExamStatus status;
    
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    public enum ExamStatus {
        SCHEDULED, ONGOING, COMPLETED
    }
}
