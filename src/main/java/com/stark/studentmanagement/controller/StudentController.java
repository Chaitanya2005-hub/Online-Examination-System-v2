package com.stark.studentmanagement.controller;

import com.stark.studentmanagement.entity.*;
import com.stark.studentmanagement.service.*;
import com.stark.studentmanagement.util.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ExamService examService;
    
    @Autowired
    private QuestionService questionService;
    
    @Autowired
    private ResultService resultService;
    
    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private LiveCodeService liveCodeService;
    
    @Autowired
    private NoticeService noticeService;
    
    @Autowired
    private AssignmentService assignmentService;
    
    @Autowired
    private GrievanceService grievanceService;
    
    @Autowired
    private AdmitCardService admitCardService;
    
    @Autowired
    private FeeService feeService;
    
    @Autowired
    private PdfService pdfService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Student Dashboard");
        model.addAttribute("theme", "theme-student");
        
        // Get notices
        List<Notice> notices = noticeService.getAllNotices();
        model.addAttribute("notices", notices);
        
        return "student/dashboard";
    }
    
    @GetMapping("/exams")
    public String exams(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Available Exams");
        model.addAttribute("theme", "theme-student");
        
        List<Exam> exams = examService.getAvailableExams();
        model.addAttribute("exams", exams);
        
        return "student/exams";
    }
    
    @GetMapping("/exam/{id}")
    public String takeExam(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Take Exam");
        model.addAttribute("theme", "theme-student");
        
        Exam exam = examService.findById(id);
        List<Question> questions = questionService.getQuestionsByExamId(id);
        
        model.addAttribute("exam", exam);
        model.addAttribute("questions", questions);
        
        return "student/exam-interface";
    }
    
    @PostMapping("/exam/{id}/submit")
    public String submitExam(@PathVariable Long id, 
                            @RequestParam(required = false) Long[] answers,
                            Authentication authentication,
                            Model model) {
        User user = userService.findByUsername(authentication.getName());
        examService.submitExam(user.getId(), id, answers);
        return "redirect:/student/results";
    }
    
    @GetMapping("/results")
    public String results(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Exam Results");
        model.addAttribute("theme", "theme-student");
        
        List<Result> results = resultService.getResultsByStudentId(user.getId());
        model.addAttribute("results", results);
        
        return "student/results";
    }
    
    @GetMapping("/attendance")
    public String attendance(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Mark Attendance");
        model.addAttribute("theme", "theme-student");
        
        return "student/attendance";
    }
    
    @PostMapping("/attendance/mark")
    public String markAttendance(@RequestParam String code, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        boolean success = attendanceService.markAttendance(user.getId(), code);
        
        if (success) {
            model.addAttribute("success", "Attendance marked successfully!");
        } else {
            model.addAttribute("error", "Invalid or expired code");
        }
        
        return attendance(authentication, model);
    }
    
    @GetMapping("/admit-card")
    public String admitCard(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Admit Card");
        model.addAttribute("theme", "theme-student");
        
        AdmitCard admitCard = admitCardService.getByStudentId(user.getId());
        model.addAttribute("admitCard", admitCard);
        
        return "student/admit-card";
    }
    
    @GetMapping("/admit-card/download")
    public void downloadAdmitCard(Authentication authentication, HttpServletResponse response) throws Exception {
        User user = userService.findByUsername(authentication.getName());
        AdmitCard admitCard = admitCardService.getByStudentId(user.getId());
        
        if (admitCard != null && admitCard.getStatus() == AdmitCard.AdmitCardStatus.RELEASED) {
            String filePath = "admit_cards/" + user.getUsername() + "_admit_card.pdf";
            new File("admit_cards").mkdirs();
            
            pdfService.generateAdmitCard(user, filePath);
            
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=admit_card.pdf");
            
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            java.nio.file.Files.copy(path, response.getOutputStream());
            response.getOutputStream().flush();
        }
    }
    
    @GetMapping("/fees")
    public String fees(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Fee Details");
        model.addAttribute("theme", "theme-student");
        
        Fee fee = feeService.getByStudentId(user.getId());
        model.addAttribute("fee", fee);
        
        return "student/fees";
    }
    
    @GetMapping("/assignments")
    public String assignments(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Assignments");
        model.addAttribute("theme", "theme-student");
        
        List<Assignment> assignments = assignmentService.getAllAssignments();
        model.addAttribute("assignments", assignments);
        
        return "student/assignments";
    }
    
    @GetMapping("/grievances")
    public String grievances(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Grievances");
        model.addAttribute("theme", "theme-student");
        
        List<Grievance> grievances = grievanceService.getByStudentId(user.getId());
        model.addAttribute("grievances", grievances);
        
        return "student/grievances";
    }
    
    @PostMapping("/grievances/submit")
    public String submitGrievance(@RequestParam String category, 
                                  @RequestParam String description,
                                  Authentication authentication,
                                  Model model) {
        User user = userService.findByUsername(authentication.getName());
        grievanceService.submitGrievance(user.getId(), category, description);
        return "redirect:/student/grievances";
    }
    
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "My Profile");
        model.addAttribute("theme", "theme-student");

        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, Authentication authentication) {
        User existingUser = userService.findByUsername(authentication.getName());
        user.setId(existingUser.getId());
        user.setUsername(existingUser.getUsername());
        user.setPassword(existingUser.getPassword());
        user.setRole(existingUser.getRole());
        user.setThemePreference(existingUser.getThemePreference());
        userService.saveUser(user);
        return "redirect:/student/profile";
    }
}
