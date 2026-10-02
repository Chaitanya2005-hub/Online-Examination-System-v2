package com.stark.studentmanagement.controller;

import com.stark.studentmanagement.entity.*;
import com.stark.studentmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ExamService examService;
    
    @Autowired
    private NoticeService noticeService;
    
    @Autowired
    private AdmitCardService admitCardService;
    
    @Autowired
    private FeeService feeService;
    
    @Autowired
    private GrievanceService grievanceService;
    
    @Autowired
    private SubjectService subjectService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Admin Dashboard");
        model.addAttribute("theme", "theme-admin");
        
        return "admin/dashboard";
    }
    
    @GetMapping("/manage-users")
    public String manageUsers(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Manage Users");
        model.addAttribute("theme", "theme-admin");
        
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("newUser", new User());
        
        return "admin/manage-users";
    }
    
    @PostMapping("/manage-users")
    public String addUser(@ModelAttribute User newUser) {
        userService.registerUser(newUser);
        return "redirect:/admin/manage-users";
    }
    
    @GetMapping("/manage-users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/manage-users";
    }
    
    @GetMapping("/schedule-exam")
    public String scheduleExam(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Schedule Exam");
        model.addAttribute("theme", "theme-admin");
        
        List<com.stark.studentmanagement.entity.Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        model.addAttribute("exam", new Exam());
        
        return "admin/schedule-exam";
    }
    
    @PostMapping("/schedule-exam")
    public String saveExam(@ModelAttribute Exam exam) {
        examService.saveExam(exam);
        return "redirect:/admin/schedule-exam";
    }
    
    @GetMapping("/post-notice")
    public String postNotice(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Post Notice");
        model.addAttribute("theme", "theme-admin");
        model.addAttribute("notice", new Notice());
        
        return "admin/post-notice";
    }
    
    @PostMapping("/post-notice")
    public String saveNotice(@ModelAttribute Notice notice, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        notice.setPostedBy(user);
        notice.setPostedDate(java.time.LocalDateTime.now());
        noticeService.saveNotice(notice);
        return "redirect:/admin/post-notice";
    }
    
    @GetMapping("/manage-admit-cards")
    public String manageAdmitCards(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Manage Admit Cards");
        model.addAttribute("theme", "theme-admin");
        
        List<User> students = userService.findByRole(User.Role.STUDENT);
        model.addAttribute("students", students);
        
        return "admin/manage-admit-cards";
    }
    
    @PostMapping("/manage-admit-cards/{studentId}")
    public String updateAdmitCard(@PathVariable Long studentId, 
                                   @RequestParam String status) {
        AdmitCard.AdmitCardStatus cardStatus = AdmitCard.AdmitCardStatus.valueOf(status);
        admitCardService.updateStatus(studentId, cardStatus);
        return "redirect:/admin/manage-admit-cards";
    }
    
    @GetMapping("/admin-fees")
    public String adminFees(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Fee Management");
        model.addAttribute("theme", "theme-admin");
        
        List<Fee> fees = feeService.getByStatus(Fee.FeeStatus.PENDING);
        model.addAttribute("fees", fees);
        
        return "admin/admin-fees";
    }
    
    @PostMapping("/admin-fees/{studentId}")
    public String updateFee(@PathVariable Long studentId, 
                            @RequestParam java.math.BigDecimal amount) {
        feeService.updatePayment(studentId, amount);
        return "redirect:/admin/admin-fees";
    }
    
    @GetMapping("/grievances")
    public String grievances(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Manage Grievances");
        model.addAttribute("theme", "theme-admin");
        
        List<Grievance> grievances = grievanceService.getByStatus(Grievance.GrievanceStatus.PENDING);
        model.addAttribute("grievances", grievances);
        
        return "admin/grievances";
    }
    
    @PostMapping("/grievances/{id}/resolve")
    public String resolveGrievance(@PathVariable Long id) {
        grievanceService.updateStatus(id, Grievance.GrievanceStatus.RESOLVED);
        return "redirect:/admin/grievances";
    }
    
    @GetMapping("/manage-subjects")
    public String manageSubjects(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Manage Subjects");
        model.addAttribute("theme", "theme-admin");
        
        List<com.stark.studentmanagement.entity.Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        model.addAttribute("subject", new com.stark.studentmanagement.entity.Subject());
        
        return "admin/manage-subjects";
    }
    
    @PostMapping("/manage-subjects")
    public String addSubject(@ModelAttribute com.stark.studentmanagement.entity.Subject subject) {
        subjectService.saveSubject(subject);
        return "redirect:/admin/manage-subjects";
    }
}
