package com.stark.studentmanagement.config;

import com.stark.studentmanagement.entity.*;
import com.stark.studentmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final NoticeRepository noticeRepository;
    private final AssignmentRepository assignmentRepository;
    private final FeeRepository feeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            String encodedPass = passwordEncoder.encode("123");

            // Seed Admin
            User admin = new User();
            admin.setUsername("stark");
            admin.setPassword(encodedPass);
            admin.setFullName("Tony Stark (Admin)");
            admin.setRole(User.Role.ADMIN);
            admin.setErpId("ADM001");
            userRepository.save(admin);

            // Seed Teacher / Faculty
            User teacher = new User();
            teacher.setUsername("bruce");
            teacher.setPassword(encodedPass);
            teacher.setFullName("Prof. Bruce Banner");
            teacher.setRole(User.Role.TEACHER);
            teacher.setDepartment("CSE");
            teacher.setErpId("TCH001");
            userRepository.save(teacher);

            // Seed Student
            User student = new User();
            student.setUsername("241801120002");
            student.setPassword(encodedPass);
            student.setFullName("Student 241801120002");
            student.setRole(User.Role.STUDENT);
            student.setDepartment("CSE");
            student.setYear(3);
            student.setSection("A");
            student.setErpId("241801120002");
            userRepository.save(student);

            // Seed Subject
            Subject subject = new Subject();
            subject.setName("Data Structures & Algorithms");
            subject.setCode("CS301");
            subject.setDepartment("CSE");
            subjectRepository.save(subject);

            // Seed Notice
            Notice notice = new Notice();
            notice.setTitle("Welcome to the New Academic Semester");
            notice.setMessage("Classes start on Monday. Please review your course materials and schedules in the portal.");
            notice.setPostedBy(admin);
            notice.setPostedDate(LocalDateTime.now());
            noticeRepository.save(notice);

            // Seed Assignment
            Assignment assignment = new Assignment();
            assignment.setTitle("Binary Search Trees Implementation");
            assignment.setDescription("Implement a BST in Java supporting insert, delete, search, and tree traversal operations.");
            assignment.setDueDate(LocalDate.now().plusDays(7));
            assignment.setCreatedBy(teacher);
            assignmentRepository.save(assignment);

            // Seed Fee
            Fee fee = new Fee();
            fee.setStudent(student);
            fee.setTotalAmount(new BigDecimal("2500.00"));
            fee.setPaidAmount(new BigDecimal("1000.00"));
            fee.setStatus(Fee.FeeStatus.PARTIAL);
            feeRepository.save(fee);

            System.out.println("DataInitializer: Initial mock data successfully seeded!");
        }
    }
}
