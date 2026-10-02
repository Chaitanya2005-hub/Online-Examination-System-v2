package com.stark.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admit_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdmitCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private User student;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdmitCardStatus status;
    
    public enum AdmitCardStatus {
        BLOCKED, RELEASED
    }
}
