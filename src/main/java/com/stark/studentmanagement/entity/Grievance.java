package com.stark.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grievances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grievance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    private String category;
    
    @Lob
    @Column(length = 5000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private GrievanceStatus status;
    
    public enum GrievanceStatus {
        PENDING, RESOLVED
    }
}
