package com.stark.studentmanagement.controller;

import com.stark.studentmanagement.entity.*;
import com.stark.studentmanagement.service.*;
import com.stark.studentmanagement.util.QrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ResultService resultService;

    @Autowired
    private LiveCodeService liveCodeService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private QrService qrService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Teacher Dashboard");
        model.addAttribute("theme", "theme-teacher");
        
        return "teacher/dashboard";
    }
    
    @GetMapping("/upload-questions")
    public String uploadQuestions(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Upload Questions");
        model.addAttribute("theme", "theme-teacher");

        List<Exam> exams = examService.getAllExams();
        model.addAttribute("exams", exams);
        model.addAttribute("question", new Question());

        return "teacher/upload-questions";
    }

    @GetMapping("/manage-exams")
    public String manageExams(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Manage Exams");
        model.addAttribute("theme", "theme-teacher");

        List<Exam> exams = examService.getAllExams();
        model.addAttribute("exams", exams);
        model.addAttribute("exam", new Exam());

        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);

        return "teacher/manage-exams";
    }

    @PostMapping("/manage-exams")
    public String createExam(@ModelAttribute Exam exam, Authentication authentication, Model model) {
        try {
            examService.saveExam(exam);
            return "redirect:/teacher/manage-exams";
        } catch (Exception e) {
            User user = userService.findByUsername(authentication.getName());
            model.addAttribute("user", user);
            model.addAttribute("title", "Manage Exams");
            model.addAttribute("theme", "theme-teacher");
            model.addAttribute("exams", examService.getAllExams());
            model.addAttribute("exam", exam);
            model.addAttribute("subjects", subjectService.getAllSubjects());
            model.addAttribute("error", "Failed to create exam: " + e.getMessage());
            return "teacher/manage-exams";
        }
    }

    @GetMapping("/manage-exams/edit/{id}")
    public String editExam(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Edit Exam");
        model.addAttribute("theme", "theme-teacher");

        Exam exam = examService.findById(id);
        model.addAttribute("exam", exam);

        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);

        return "teacher/edit-exam";
    }

    @PostMapping("/manage-exams/edit/{id}")
    public String updateExam(@PathVariable Long id, @ModelAttribute Exam exam, Authentication authentication) {
        exam.setId(id);
        examService.saveExam(exam);
        return "redirect:/teacher/manage-exams";
    }

    @GetMapping("/manage-exams/delete/{id}")
    public String deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return "redirect:/teacher/manage-exams";
    }
    
    @PostMapping("/upload-questions")
    public String saveQuestion(@ModelAttribute Question question, Authentication authentication, Model model) {
        try {
            questionService.saveQuestion(question);
            return "redirect:/teacher/upload-questions";
        } catch (Exception e) {
            User user = userService.findByUsername(authentication.getName());
            model.addAttribute("user", user);
            model.addAttribute("title", "Upload Questions");
            model.addAttribute("theme", "theme-teacher");
            model.addAttribute("exams", examService.getAllExams());
            model.addAttribute("question", question);
            model.addAttribute("error", "Failed to save question: " + e.getMessage());
            return "teacher/upload-questions";
        }
    }
    
    @GetMapping("/manage-questions")
    public String manageQuestions(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Manage Questions");
        model.addAttribute("theme", "theme-teacher");
        
        List<Question> questions = questionService.getQuestionsByExamId(null);
        model.addAttribute("questions", questions);
        
        return "teacher/manage-questions";
    }
    
    @GetMapping("/mark-attendance")
    public String markAttendance(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Mark Attendance");
        model.addAttribute("theme", "theme-teacher");

        List<User> students = userService.findByRole(User.Role.STUDENT);
        model.addAttribute("students", students);

        return "teacher/mark-attendance";
    }

    @PostMapping("/mark-attendance/manual")
    public String markAttendanceManual(@RequestParam("date") String dateStr,
                                       @RequestParam Map<String, String> attendanceMap,
                                       Authentication authentication) {
        User teacher = userService.findByUsername(authentication.getName());
        LocalDate date = LocalDate.parse(dateStr);

        attendanceMap.forEach((key, value) -> {
            if (key.startsWith("attendance[")) {
                Long studentId = Long.parseLong(key.substring(11, key.length() - 1));
                Attendance.AttendanceStatus status = Attendance.AttendanceStatus.valueOf(value);

                // Check if attendance already exists for this student on this date
                Attendance existingAttendance = attendanceService.findByStudentIdAndDate(studentId, date);
                if (existingAttendance == null) {
                    Attendance attendance = new Attendance();
                    User student = new User();
                    student.setId(studentId);
                    attendance.setStudent(student);
                    attendance.setDate(date);
                    attendance.setStatus(status);
                    attendance.setMarkedBy(teacher);
                    attendanceService.saveAttendance(attendance);
                }
            }
        });

        return "redirect:/teacher/mark-attendance";
    }
    
    @GetMapping(value = "/qr-code", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] generateQrCode() throws Exception {
        String code = liveCodeService.generateLiveCode();
        return qrService.generateQrCodeBytes(code, 300, 300);
    }
    
    @GetMapping("/exam-results")
    public String examResults(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Exam Results");
        model.addAttribute("theme", "theme-teacher");
        
        List<Exam> exams = examService.getAllExams();
        model.addAttribute("exams", exams);
        
        return "teacher/exam-results";
    }
    
    @GetMapping("/exam-results/{id}")
    public String examResultsDetail(@PathVariable Long id, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Exam Results Detail");
        model.addAttribute("theme", "theme-teacher");
        
        List<Result> results = resultService.getResultsByExamId(id);
        model.addAttribute("results", results);
        
        return "teacher/exam-results-detail";
    }
    
    @GetMapping("/assignments")
    public String assignments(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Manage Assignments");
        model.addAttribute("theme", "theme-teacher");
        
        List<Assignment> assignments = assignmentService.getAssignmentsByTeacher(user.getId());
        model.addAttribute("assignments", assignments);
        model.addAttribute("assignment", new Assignment());
        
        return "teacher/assignments";
    }
    
    @PostMapping("/assignments")
    public String createAssignment(@ModelAttribute Assignment assignment, Authentication authentication, Model model) {
        try {
            User user = userService.findByUsername(authentication.getName());
            assignment.setCreatedBy(user);
            assignmentService.saveAssignment(assignment);
            return "redirect:/teacher/assignments";
        } catch (Exception e) {
            User user = userService.findByUsername(authentication.getName());
            model.addAttribute("user", user);
            model.addAttribute("title", "Manage Assignments");
            model.addAttribute("theme", "theme-teacher");
            model.addAttribute("assignments", assignmentService.getAssignmentsByTeacher(user.getId()));
            model.addAttribute("assignment", assignment);
            model.addAttribute("error", "Failed to create assignment: " + e.getMessage());
            return "teacher/assignments";
        }
    }
    
    @GetMapping("/student-progress")
    public String studentProgress(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Student Progress");
        model.addAttribute("theme", "theme-teacher");

        List<User> students = userService.findByRole(User.Role.STUDENT);
        model.addAttribute("students", students);

        return "teacher/student-progress";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "My Profile");
        model.addAttribute("theme", "theme-teacher");

        return "teacher/profile";
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
        return "redirect:/teacher/profile";
    }
}
