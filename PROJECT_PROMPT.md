# Complete Project Prompt: Online Examination & Academic Management System

## Project Overview
Build a comprehensive desktop-based Online Examination and Academic Management System for educational institutions. The system should digitize and streamline academic processes including online examinations, student information management, fee tracking, attendance recording, and faculty administration.

## Technology Stack
- **Language**: Java 17+
- **Frontend Framework**: JavaFX with FXML
- **Database**: MySQL 8.0+
- **Database Connectivity**: JDBC
- **Build Tool**: Maven
- **PDF Generation**: iText 5.5.13.3
- **QR Code Generation**: ZXing 3.5.1
- **JSON Parsing**: Gson 2.10.1
- **IDE**: IntelliJ IDEA (recommended)

## Project Structure
```
src/main/java/com/stark/exam/
├── db/
│   └── DBConnection.java (Singleton database connection)
├── model/
│   └── User.java (User entity with fields: id, username, fullName, role, erpId, year, department, section, photoPath)
├── ui/
│   ├── login/
│   │   └── LoginController.java
│   ├── student/
│   │   ├── StudentDashboardController.java
│   │   ├── ExamInterfaceController.java (with anti-cheating)
│   │   ├── ExamListController.java
│   │   ├── AttendanceController.java
│   │   ├── AdmitCardController.java
│   │   ├── FeeController.java
│   │   ├── PerformanceController.java
│   │   ├── ProfileController.java
│   │   ├── TimetableController.java
│   │   ├── GrievanceController.java
│   │   └── StudentAssignmentController.java
│   ├── teacher/
│   │   ├── TeacherDashboardController.java
│   │   ├── UploadQuestionsController.java
│   │   ├── ManageQuestionsController.java
│   │   ├── MarkAttendanceController.java (with QR code)
│   │   ├── ExamResultsController.java
│   │   ├── AssignmentController.java
│   │   ├── StudentProgressController.java
│   │   └── AnswerSheetController.java
│   └── author/
│       ├── AuthorDashboardController.java
│       ├── ScheduleExamController.java
│       ├── PostNoticeController.java
│       ├── ManageUsersController.java
│       ├── ManageAdmitCardsController.java
│       ├── AdminFeeController.java
│       ├── AdminAttendanceController.java
│       ├── SystemReportsController.java
│       └── ManageSubjectsController.java
├── util/
│   ├── PdfService.java (PDF generation for admit cards)
│   └── QrService.java (QR code generation for attendance)
├── App.java (Main JavaFX application)
├── AppLauncher.java (Alternative launcher)
├── DemoLauncher.java (Testing launcher - opens 3 windows)
└── Main.java (Entry point)

src/main/resources/fxml/
├── login.fxml
├── student_dashboard.fxml
├── teacher_dashboard.fxml
├── author_dashboard.fxml
├── exam_interface.fxml
├── exam_list.fxml
├── attendance.fxml
├── admit_card.fxml
├── fee_details.fxml
├── performance.fxml
├── profile.fxml
├── timetable.fxml
├── grievance.fxml
├── student_assignment.fxml
├── upload_questions.fxml
├── manage_questions.fxml
├── mark_attendance.fxml
├── exam_results.fxml
├── teacher_assignment.fxml
├── student_progress.fxml
├── answer_sheet.fxml
├── schedule_exam.fxml
├── post_notice.fxml
├── manage_users.fxml
├── manage_admit_cards.fxml
├── admin_fees.fxml
├── admin_attendance.fxml
├── system_reports.fxml
└── manage_subjects.fxml
```

## Database Schema

### Tables Required:

1. **users**
   - id (INT, AUTO_INCREMENT, PRIMARY KEY)
   - username (VARCHAR, UNIQUE)
   - password (VARCHAR)
   - full_name (VARCHAR)
   - role (ENUM: 'student', 'teacher', 'author')
   - erp_id (VARCHAR)
   - year (INT)
   - department (VARCHAR)
   - section (VARCHAR)
   - photo_path (VARCHAR, nullable)

2. **subjects**
   - id (INT, AUTO_INCREMENT, PRIMARY KEY)
   - name (VARCHAR)
   - code (VARCHAR)
   - department (VARCHAR)

3. **exams**
   - id (INT, AUTO_INCREMENT, PRIMARY KEY)
   - title (VARCHAR)
   - exam_date (DATE)
   - start_time (TIME)
   - duration_minutes (INT)
   - status (ENUM: 'scheduled', 'ongoing', 'completed')

4. **questions**
   - id (INT, AUTO_INCREMENT, PRIMARY KEY)
   - exam_id (INT, FOREIGN KEY)
   - question_text (TEXT)
   - option_a (VARCHAR)
   - option_b (VARCHAR)
   - option_c (VARCHAR)
   - option_d (VARCHAR)
   - correct_answer (VARCHAR)

5. **results**
   - id (INT, AUTO_INCREMENT, PRIMARY KEY)
   - student_id (INT, FOREIGN KEY)
   - exam_id (INT, FOREIGN KEY)
   - score (INT)
   - total_marks (INT)
   - status (VARCHAR)
   - security_warnings (INT)

6. **attendance**
   - id (INT, AUTO_INCREMENT, PRIMARY KEY)
   - student_id (INT, FOREIGN KEY)
   - date (DATE)
   - status (ENUM: 'Present', 'Absent')
   - marked_by (INT, FOREIGN KEY)

7. **live_codes**
   - id (INT, PRIMARY KEY)
   - code (VARCHAR) - Generated QR code for attendance

8. **notices**
   - id (INT, AUTO_INCREMENT, PRIMARY KEY)
   - title (VARCHAR)
   - message (TEXT)
   - posted_by (INT, FOREIGN KEY)
   - posted_date (TIMESTAMP)

9. **grievances**
   - id (INT, AUTO_INCREMENT, PRIMARY KEY)
   - student_id (INT, FOREIGN KEY)
   - category (VARCHAR)
   - description (TEXT)
   - status (ENUM: 'pending', 'resolved')

10. **assignments**
    - id (INT, AUTO_INCREMENT, PRIMARY KEY)
    - title (VARCHAR)
    - description (TEXT)
    - due_date (DATE)
    - created_by (INT, FOREIGN KEY)

11. **submissions**
    - id (INT, AUTO_INCREMENT, PRIMARY KEY)
    - assignment_id (INT, FOREIGN KEY)
    - student_id (INT, FOREIGN KEY)
    - submission_text (TEXT)
    - submitted_date (TIMESTAMP)

12. **admit_cards**
    - id (INT, AUTO_INCREMENT, PRIMARY KEY)
    - student_id (INT, FOREIGN KEY, UNIQUE)
    - status (ENUM: 'Blocked', 'Released')

13. **fees**
    - id (INT, AUTO_INCREMENT, PRIMARY KEY)
    - student_id (INT, FOREIGN KEY)
    - total_amount (DECIMAL)
    - paid_amount (DECIMAL)
    - status (ENUM: 'pending', 'paid', 'partial')

## Core Features

### 1. Authentication Module
- Login system with role-based access control
- Three roles: Student, Teacher, Admin (Author)
- Session management using User object passed via reflection
- Dashboard routing based on user role

### 2. Student Module

#### Online Examination System
- View available exams
- Take objective-type exams (MCQs)
- **Anti-Cheating Mechanism**:
  - 3-Strike Rule for focus loss detection
  - Warning 1: Alert popup
  - Warning 2: Final warning
  - Warning 3: Auto-terminate and submit exam
  - Uses JavaFX Stage focus property listener
- Countdown timer
- Navigation between questions
- Instant result display after submission
- Security warning tracking in database

#### Student Dashboard
- View profile information
- Check fee status
- View attendance records
- Access exam history and results
- View notices/announcements
- Download admit card (PDF)
- Submit grievances
- View and submit assignments
- View exam timetable

#### Attendance System
- Enter 4-digit code displayed on teacher's screen
- QR code scanning alternative
- Real-time validation against live_codes table
- Attendance status tracking

#### Admit Card System
- View admit card status (Blocked/Released)
- Download admit card as PDF
- PDF includes: student name, roll number, department, date, instructions
- Uses iText library for PDF generation

### 3. Teacher Module

#### Teacher Dashboard
- Access all teacher-specific modules
- View assigned subjects
- Manage question bank
- View student progress
- Schedule and conduct exams

#### Question Management
- Upload questions for exams
- Edit/delete existing questions
- MCQ format with 4 options
- Mark correct answer
- Organize by exam/subject

#### Exam Management
- Create and schedule exams
- Set exam date, time, and duration
- Manage exam status (scheduled/ongoing/completed)
- View exam results

#### Attendance System (QR-based)
- Generate dynamic QR codes for attendance
- Codes refresh every 10 seconds
- Display 4-digit code alongside QR
- Update live_codes table in real-time
- Mark attendance manually via checkbox list
- Date-based attendance tracking

#### Assignment Management
- Create assignments with title, description, due date
- View student submissions
- Grade assignments

#### Student Progress
- View individual student performance
- Track attendance records
- Monitor exam results
- Export reports

### 4. Admin Module

#### Admin Dashboard
- Central control panel
- Access all admin modules
- Reuse teacher controllers for question/assignment management

#### User Management
- Add new users (students/teachers)
- Set user credentials
- Assign roles, departments, years
- View all users
- Auto-generate admit card entry (Blocked status)

#### Exam Scheduling
- Create exam schedules
- Link to subjects
- Set duration and timing
- Manage exam lifecycle

#### Notice Board
- Post announcements for all users
- Title and message format
- Timestamp tracking
- Global visibility

#### Admit Card Management
- Block/Release admit cards for students
- Bulk operations
- Status tracking

#### Fee Management
- View fee status of all students
- Track paid/pending amounts
- Update fee payments
- Generate fee reports
- Block exam access for defaulters

#### System Reports
- Attendance reports
- Fee reports
- Exam results summary
- Export functionality

#### Subject Management
- Add new subjects
- Set subject codes
- Assign to departments

## Key Technical Implementations

### Database Connection (Singleton Pattern)
```java
package com.stark.exam.db;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/exam_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "your_password";
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }
}
```

### User Model
```java
package com.stark.exam.model;

public class User {
    private int id;
    private String username;
    private String fullName;
    private String role;
    private String erpId;
    private int year;
    private String department;
    private String section;
    private String photoPath;

    // Getters and Setters
    // Constructor
}
```

### Anti-Cheating Implementation
```java
private void setupSecurityListener() {
    parentStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
        if (!isNowFocused && !isSubmitted) {
            warningCount++;
            if (warningCount >= MAX_WARNINGS) {
                Platform.runLater(this::terminateExam);
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Security Alert");
                    alert.setHeaderText("WARNING " + warningCount + "/" + MAX_WARNINGS);
                    alert.setContentText("You switched tabs/windows!");
                    alert.showAndWait();
                });
            }
        }
    });
}
```

### Reflection-based User Passing
```java
// In LoginController
Object controller = loader.getController();
try {
    controller.getClass().getMethod("setUser", User.class).invoke(controller, user);
} catch (Exception e) {
    System.err.println("Warning: Controller does not have setUser(User) method.");
}
```

### QR Code Generation
```java
package com.stark.exam.util;

public class QrService {
    public static Image generateQr(String text, int width, int height) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            return SwingFXUtils.toFXImage(bufferedImage, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
```

### PDF Generation for Admit Cards
```java
package com.stark.exam.util;

public class PdfService {
    public static void generateAdmitCard(User user, String filePath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Add university header
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLUE);
        Paragraph title = new Paragraph("UNIVERSITY NAME", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Add student details
        // Add instructions
        // Add footer

        document.close();
    }
}
```

## Maven Dependencies (pom.xml)
```xml
<dependencies>
    <!-- JavaFX Controls -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.2</version>
    </dependency>

    <!-- JavaFX FXML -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>17.0.2</version>
    </dependency>

    <!-- JavaFX Swing (for QR code conversion) -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-swing</artifactId>
        <version>17.0.2</version>
    </dependency>

    <!-- MySQL Connector -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.0.33</version>
    </dependency>

    <!-- QR Code Generator -->
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>core</artifactId>
        <version>3.5.1</version>
    </dependency>
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>javase</artifactId>
        <version>3.5.1</version>
    </dependency>

    <!-- JSON Parsing -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>

    <!-- PDF Generation -->
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itextpdf</artifactId>
        <version>5.5.13.3</version>
    </dependency>
</dependencies>
```

## UI Design Guidelines

### Login Screen
- Username and password fields
- Role auto-detection from database
- Clean, centered layout

### Dashboards
- Grid-based menu with icon buttons
- User name display at top
- Role-specific color themes:
  - Student: Blue theme
  - Teacher: Green theme
  - Admin: Purple/Indigo theme

### Exam Interface
- Full-screen mode
- Question display area
- 4 radio button options
- Timer countdown (prominent)
- Navigation buttons (Previous/Next)
- Submit button
- Security warning overlay

### Forms
- Consistent field labeling
- Validation messages
- Success/error alerts
- Clean spacing and alignment

## Security Features

1. **Password Storage**: Plain text (Note: In production, use bcrypt/Argon2)
2. **SQL Injection Prevention**: Use PreparedStatement
3. **Session Management**: User object passed via reflection
4. **Exam Integrity**: Focus loss detection with 3-strike rule
5. **Role-Based Access**: Separate dashboards and permissions

## Installation & Setup Instructions

### Prerequisites
- Java JDK 17 or higher
- MySQL Server 8.0+
- Maven (or IntelliJ IDEA)

### Database Setup
1. Create database: `CREATE DATABASE exam_system;`
2. Run schema script to create all tables
3. Insert initial admin user
4. Configure DBConnection.java with your MySQL credentials

### IDE Configuration
1. Open project in IntelliJ IDEA
2. Set Project SDK to Java 17
3. Reload Maven dependencies
4. Mark `src/main/resources` as Resources Root
5. Run App.java or Main.java

### Testing
- Use DemoLauncher.java to open all three dashboards simultaneously
- Pre-fill credentials for quick testing

## Additional Features to Consider

1. **Email Notifications**: For exam schedules, fee reminders
2. **Data Export**: Excel/CSV exports for reports
3. **Backup System**: Database backup functionality
4. **Dark Mode**: Theme switching
5. **Mobile App**: Companion mobile application
6. **Analytics**: Performance charts and graphs
7. **Chat System**: Teacher-student communication
8. **Video Conferencing**: Integration for online classes
9. **Plagiarism Detection**: For assignment submissions
10. **Biometric Attendance**: Fingerprint/Face recognition

## Known Limitations

1. Passwords stored in plain text (should be hashed)
2. No two-factor authentication
3. Limited to MCQ exams (no descriptive questions)
4. No automatic backup system
5. Manual database configuration required
6. No cloud deployment support

## Testing Checklist

- [ ] Login with all three roles
- [ ] Student: Take exam with anti-cheating
- [ ] Student: Download admit card
- [ ] Student: Mark attendance via code
- [ ] Teacher: Upload questions
- [ ] Teacher: Generate QR codes for attendance
- [ ] Teacher: View results
- [ ] Admin: Add new user
- [ ] Admin: Schedule exam
- [ ] Admin: Release admit card
- [ ] Admin: Post notice
- [ ] Admin: Update fee status

## Deployment Notes

1. Package as executable JAR with JavaFX
2. Include MySQL driver in classpath
3. Provide database setup script
4. Create user documentation
5. Test on target operating systems (Windows, macOS, Linux)

---

This prompt provides a complete blueprint for recreating the Online Examination System. Use this as a reference for developers or AI assistants to build an identical system with all features, architecture, and functionality.
