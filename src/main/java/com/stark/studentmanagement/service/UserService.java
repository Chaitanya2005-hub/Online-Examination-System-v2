package com.stark.studentmanagement.service;

import com.stark.studentmanagement.entity.*;
import com.stark.studentmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AdmitCardRepository admitCardRepository;
    
    @Autowired
    private FeeRepository feeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private GrievanceRepository grievanceRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private SubmissionRepository submissionRepository;
    
    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        
        // Auto-generate admit card entry for students
        if (user.getRole() == User.Role.STUDENT) {
            AdmitCard admitCard = new AdmitCard();
            admitCard.setStudent(savedUser);
            admitCard.setStatus(AdmitCard.AdmitCardStatus.BLOCKED);
            admitCardRepository.save(admitCard);
            
            // Auto-generate fee entry
            Fee fee = new Fee();
            fee.setStudent(savedUser);
            fee.setTotalAmount(java.math.BigDecimal.valueOf(50000));
            fee.setPaidAmount(java.math.BigDecimal.ZERO);
            fee.setStatus(Fee.FeeStatus.PENDING);
            feeRepository.save(fee);
        }
        
        return savedUser;
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    @Transactional
    public User saveUser(User user) {
        // Only encode password if it's a new user or password is being changed
        if (user.getId() == null || (user.getPassword() != null && !user.getPassword().isEmpty() && !user.getPassword().startsWith("$2a$"))) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
    
    public List<User> findStudentsByFilter(String department, String section, Integer year) {
        List<User> students = userRepository.findByRole(User.Role.STUDENT);
        return students.stream()
                .filter(s -> (department == null || department.isEmpty() || department.equalsIgnoreCase(s.getDepartment())))
                .filter(s -> (section == null || section.isEmpty() || section.equalsIgnoreCase(s.getSection())))
                .filter(s -> (year == null || year.equals(s.getYear())))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Transactional
    public void deleteUser(Long id) {
        admitCardRepository.findByStudentId(id).ifPresent(admitCardRepository::delete);
        feeRepository.findByStudentId(id).ifPresent(feeRepository::delete);

        List<Attendance> attList = attendanceRepository.findByStudentId(id);
        if (attList != null && !attList.isEmpty()) {
            attendanceRepository.deleteAll(attList);
        }

        List<Grievance> gList = grievanceRepository.findByStudentId(id);
        if (gList != null && !gList.isEmpty()) {
            grievanceRepository.deleteAll(gList);
        }

        List<Result> rList = resultRepository.findByStudentId(id);
        if (rList != null && !rList.isEmpty()) {
            resultRepository.deleteAll(rList);
        }

        List<Submission> sList = submissionRepository.findByStudentId(id);
        if (sList != null && !sList.isEmpty()) {
            submissionRepository.deleteAll(sList);
        }

        userRepository.deleteById(id);
    }
}
