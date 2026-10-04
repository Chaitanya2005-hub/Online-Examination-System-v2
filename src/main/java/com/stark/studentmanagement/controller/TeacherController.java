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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private GeminiQuestionGeneratorService geminiQuestionGeneratorService;

    @Autowired
    private ProctoringService proctoringService;

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

    @PostMapping("/auto-generate-questions")
    public String autoGenerateQuestions(@RequestParam Long examId,
                                        @RequestParam(defaultValue = "10") int count,
                                        @RequestParam(required = false) String topicFocus,
                                        @RequestParam(defaultValue = "MEDIUM") String difficultyLevel,
                                        @RequestParam(required = false) String apiKey,
                                        RedirectAttributes redirectAttributes) {
        try {
            List<Question> generated = geminiQuestionGeneratorService.generateAndSaveQuestions(examId, count, topicFocus, difficultyLevel, apiKey);
            redirectAttributes.addFlashAttribute("success", "✨ Successfully auto-generated and saved " + generated.size() + " (" + difficultyLevel + ") AI questions to the database!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Google Gemini AI Error: " + e.getMessage());
        }
        return "redirect:/teacher/upload-questions";
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
    public String manageQuestions(@RequestParam(required = false) Long examId, Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Manage Questions");
        model.addAttribute("theme", "theme-teacher");
        
        List<Question> questions = (examId != null) ? questionService.getQuestionsByExamId(examId) : questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        model.addAttribute("exams", examService.getAllExams());
        model.addAttribute("selectedExamId", examId);
        
        return "teacher/manage-questions";
    }

    @GetMapping("/manage-questions/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return "redirect:/teacher/manage-questions";
    }
    
    @GetMapping("/mark-attendance")
    public String markAttendance(@RequestParam(required = false) String department,
                                 @RequestParam(required = false) String section,
                                 @RequestParam(required = false) Integer year,
                                 @RequestParam(required = false) String dateStr,
                                 Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Mark Attendance");
        model.addAttribute("theme", "theme-teacher");

        LocalDate date = (dateStr != null && !dateStr.trim().isEmpty()) ? LocalDate.parse(dateStr) : LocalDate.now();
        List<User> students = userService.findStudentsByFilter(department, section, year);

        Map<Long, Attendance.AttendanceStatus> attendanceStatusMap = new java.util.HashMap<>();
        for (User student : students) {
            Attendance att = attendanceService.findByStudentIdAndDate(student.getId(), date);
            if (att != null) {
                attendanceStatusMap.put(student.getId(), att.getStatus());
            }
        }

        model.addAttribute("students", students);
        model.addAttribute("selectedDept", department);
        model.addAttribute("selectedSection", section);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedDate", date.toString());
        model.addAttribute("attendanceStatusMap", attendanceStatusMap);

        return "teacher/mark-attendance";
    }

    @PostMapping("/mark-attendance/manual")
    public String markAttendanceManual(@RequestParam("date") String dateStr,
                                       @RequestParam(required = false) String department,
                                       @RequestParam(required = false) String section,
                                       @RequestParam(required = false) Integer year,
                                       @RequestParam Map<String, String> attendanceMap,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        User teacher = userService.findByUsername(authentication.getName());
        LocalDate date = LocalDate.parse(dateStr);
        int count = 0;

        for (Map.Entry<String, String> entry : attendanceMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
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
                } else {
                    existingAttendance.setStatus(status);
                    existingAttendance.setMarkedBy(teacher);
                    attendanceService.saveAttendance(existingAttendance);
                }
                count++;
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "✅ Attendance saved successfully for " + count + " students!");
        
        StringBuilder redirectUrl = new StringBuilder("redirect:/teacher/mark-attendance?dateStr=").append(dateStr);
        if (department != null && !department.isEmpty()) redirectUrl.append("&department=").append(department);
        if (section != null && !section.isEmpty()) redirectUrl.append("&section=").append(section);
        if (year != null) redirectUrl.append("&year=").append(year);

        return redirectUrl.toString();
    }
    
    @GetMapping(value = "/qr-code", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] generateQrCode() {
        try {
            String code = liveCodeService.generateLiveCode();
            return qrService.generateQrCodeBytes(code, 300, 300);
        } catch (Exception e) {
            try {
                return qrService.generateQrCodeBytes("ATTENDANCE", 300, 300);
            } catch (Exception ex) {
                return new byte[0];
            }
        }
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

    @GetMapping("/live-proctoring")
    public String liveProctoring(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName());
        model.addAttribute("user", user);
        model.addAttribute("title", "Live WebCam Proctoring");
        model.addAttribute("theme", "theme-teacher");

        model.addAttribute("sessions", proctoringService.getActiveSessions());

        return "teacher/live-proctoring";
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
