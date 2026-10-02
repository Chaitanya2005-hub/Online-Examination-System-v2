package com.stark.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "live_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiveCode {
    
    @Id
    private String code;
    
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "expires_at")
    private java.time.LocalDateTime expiresAt;
}
