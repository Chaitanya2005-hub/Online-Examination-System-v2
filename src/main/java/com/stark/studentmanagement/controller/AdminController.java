package com.stark.studentmanagement.controller;

import com.stark.studentmanagement.entity.*;
import com.stark.studentmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    
    @Autowired
    private ProctoringService proctoringService;
    
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
        Map<Long, AdmitCard.AdmitCardStatus> admitCardStatusMap = new HashMap<>();
        for (User student : students) {
            AdmitCard card = admitCardService.getByStudentId(student.getId());
            if (card == null) {
                card = new AdmitCard();
                card.setStudent(student);
                card.setStatus(AdmitCard.AdmitCardStatus.BLOCKED);
                card = admitCardService.saveAdmitCard(card);
            }
            admitCardStatusMap.put(student.getId(), card.getStatus());
        }
        model.addAttribute("students", students);
        model.addAttribute("admitCardStatusMap", admitCardStatusMap);
        
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
        
        List<Fee> fees = feeService.getAllFees();
        model.addAttribute("fees", fees);
        
        List<User> allStudents = userService.findByRole(User.Role.STUDENT);
        model.addAttribute("allStudents", allStudents);
        
        java.math.BigDecimal totalCollected = fees.stream()
                .map(f -> f.getPaidAmount() != null ? f.getPaidAmount() : java.math.BigDecimal.ZERO)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                
        long pendingCount = fees.stream()
                .filter(f -> f.getApprovalStatus() == Fee.ApprovalStatus.PENDING || f.getApprovalStatus() == null)
                .count();
                
        long approvedCount = fees.stream()
                .filter(f -> f.getApprovalStatus() == Fee.ApprovalStatus.APPROVED)
                .count();
                
        long disapprovedCount = fees.stream()
                .filter(f -> f.getApprovalStatus() == Fee.ApprovalStatus.DISAPPROVED)
                .count();
                
        model.addAttribute("totalCollected", totalCollected);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("disapprovedCount", disapprovedCount);
        
        return "admin/admin-fees";
    }

    @GetMapping({"/live-monitoring", "/live-proctoring"})
    public String liveMonitoring(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Live WebCam Monitoring");
        model.addAttribute("theme", "theme-admin");
        model.addAttribute("sessions", proctoringService.getActiveSessions());
        return "admin/live-monitoring";
    }
    
    @PostMapping("/admin-fees/add")
    public String addFee(@RequestParam Long studentId,
                         @RequestParam java.math.BigDecimal totalAmount,
                         @RequestParam(required = false, defaultValue = "0") java.math.BigDecimal paidAmount,
                         @RequestParam String approvalStatus,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        User student = userService.findById(studentId);
        if (student != null) {
            Fee fee = feeService.getByStudentId(studentId);
            if (fee == null) {
                fee = new Fee();
                fee.setStudent(student);
            }
            fee.setTotalAmount(totalAmount);
            fee.setPaidAmount(paidAmount);
            fee.setApprovalStatus(Fee.ApprovalStatus.valueOf(approvalStatus));
            
            if (paidAmount.compareTo(totalAmount) >= 0 && totalAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                fee.setStatus(Fee.FeeStatus.PAID);
            } else if (paidAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
                fee.setStatus(Fee.FeeStatus.PARTIAL);
            } else {
                fee.setStatus(Fee.FeeStatus.PENDING);
            }
            feeService.saveFee(fee);
            redirectAttributes.addFlashAttribute("successMessage", "Fee record assigned/updated for " + student.getFullName() + "!");
        }
        return "redirect:/admin/admin-fees";
    }

    @PostMapping("/admin-fees/generate-mock")
    public String generateMockFees(org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        List<User> students = userService.findByRole(User.Role.STUDENT);
        int count = 0;
        int index = 0;
        for (User st : students) {
            Fee f = feeService.getByStudentId(st.getId());
            if (f == null) {
                f = new Fee();
                f.setStudent(st);
            }
            if (st.getUsername().equals("241801120002")) {
                f.setTotalAmount(new java.math.BigDecimal("50000.00"));
                f.setPaidAmount(new java.math.BigDecimal("35000.00"));
                f.setStatus(Fee.FeeStatus.PARTIAL);
                f.setApprovalStatus(Fee.ApprovalStatus.APPROVED);
            } else if (index % 3 == 0) {
                f.setTotalAmount(new java.math.BigDecimal("50000.00"));
                f.setPaidAmount(new java.math.BigDecimal("50000.00"));
                f.setStatus(Fee.FeeStatus.PAID);
                f.setApprovalStatus(Fee.ApprovalStatus.APPROVED);
            } else if (index % 3 == 1) {
                f.setTotalAmount(new java.math.BigDecimal("52000.00"));
                f.setPaidAmount(new java.math.BigDecimal("25000.00"));
                f.setStatus(Fee.FeeStatus.PARTIAL);
                f.setApprovalStatus(Fee.ApprovalStatus.PENDING);
            } else {
                f.setTotalAmount(new java.math.BigDecimal("48000.00"));
                f.setPaidAmount(new java.math.BigDecimal("0.00"));
                f.setStatus(Fee.FeeStatus.PENDING);
                f.setApprovalStatus(Fee.ApprovalStatus.DISAPPROVED);
            }
            feeService.saveFee(f);
            count++;
            index++;
        }
        redirectAttributes.addFlashAttribute("successMessage", "Successfully populated mock fee data for " + count + " students!");
        return "redirect:/admin/admin-fees";
    }
    
    @PostMapping("/admin-fees/edit")
    public String editFee(@RequestParam Long feeId,
                          @RequestParam java.math.BigDecimal totalAmount,
                          @RequestParam java.math.BigDecimal paidAmount,
                          @RequestParam String approvalStatus,
                          org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        feeService.editFee(feeId, totalAmount, paidAmount, Fee.ApprovalStatus.valueOf(approvalStatus));
        redirectAttributes.addFlashAttribute("successMessage", "Fee details updated successfully!");
        return "redirect:/admin/admin-fees";
    }
    
    @PostMapping("/admin-fees/{feeId}/approve")
    public String approveFee(@PathVariable Long feeId,
                             org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        feeService.updateApprovalStatus(feeId, Fee.ApprovalStatus.APPROVED);
        redirectAttributes.addFlashAttribute("successMessage", "Fee record APPROVED successfully!");
        return "redirect:/admin/admin-fees";
    }
    
    @PostMapping("/admin-fees/{feeId}/disapprove")
    public String disapproveFee(@PathVariable Long feeId,
                                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        feeService.updateApprovalStatus(feeId, Fee.ApprovalStatus.DISAPPROVED);
        redirectAttributes.addFlashAttribute("successMessage", "Fee record DISAPPROVED!");
        return "redirect:/admin/admin-fees";
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
