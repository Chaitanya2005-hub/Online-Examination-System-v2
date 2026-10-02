package com.stark.studentmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(name = "erp_id")
    private String erpId;
    
    private Integer year;
    
    private String department;
    
    private String section;
    
    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "theme_preference")
    private String themePreference;

    public enum Role {
        STUDENT, TEACHER, ADMIN
    }
}
