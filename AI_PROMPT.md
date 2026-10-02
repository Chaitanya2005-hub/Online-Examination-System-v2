# AI Prompt: Build Online Examination System

Create a complete desktop-based Online Examination and Academic Management System for educational institutions.

## Technology Stack
- Java 17+ with JavaFX (FXML)
- MySQL 8.0+ with JDBC
- Maven for build management
- iText 5.5.13.3 for PDF generation
- ZXing 3.5.1 for QR code generation
- Gson 2.10.1 for JSON parsing

## Core Requirements

### 1. Three-Role Authentication System
- **Student**: Take exams, view results, download admit cards, mark attendance
- **Teacher**: Upload questions, generate QR attendance codes, view results, manage assignments
- **Admin**: Manage users, schedule exams, release admit cards, post notices, manage fees

### 2. Online Examination Module (STUDENT)
- View available exams with countdown timer
- Take MCQ exams with 4 options per question
- **Anti-Cheating System**: 3-strike rule for focus loss detection
  - Warning 1: Alert popup
  - Warning 2: Final warning
  - Warning 3: Auto-terminate and submit exam
- Navigate between questions (Previous/Next)
- Instant results after submission
- Track security warnings in database

### 3. Attendance System (TEACHER)
- Generate dynamic QR codes that refresh every 10 seconds
- Display 4-digit code alongside QR
- Students can enter code or scan QR to mark attendance
- Manual attendance marking via checkbox list
- Update live_codes table in real-time

### 4. Admit Card System (STUDENT + ADMIN)
- Admin can block/release admit cards
- Students can download admit card as PDF
- PDF includes: name, roll number, department, date, instructions
- Use iText library for PDF generation

### 5. Question Bank Management (TEACHER)
- Upload MCQ questions with 4 options
- Mark correct answer
- Edit/delete existing questions
- Organize by exam/subject

### 6. User Management (ADMIN)
- Add students and teachers
- Set credentials, role, department, year
- Auto-generate admit card entry (Blocked status)

### 7. Fee Management (ADMIN)
- Track fee status (paid/pending/partial)
- Update payments
- Generate fee reports
- Block exam access for defaulters

### 8. Notice Board (ADMIN)
- Post announcements visible to all users
- Title and message format
- Timestamp tracking

### 9. Assignment System (TEACHER + STUDENT)
- Teachers create assignments with due dates
- Students submit assignments
- Teachers view submissions

### 10. Grievance System (STUDENT)
- Submit grievances with category and description
- Track status (pending/resolved)

## Database Schema

Create the following tables:

**users**: id, username, password, full_name, role, erp_id, year, department, section, photo_path
**subjects**: id, name, code, department
**exams**: id, title, exam_date, start_time, duration_minutes, status
**questions**: id, exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer
**results**: id, student_id, exam_id, score, total_marks, status, security_warnings
**attendance**: id, student_id, date, status, marked_by
**live_codes**: id, code (for QR attendance)
**notices**: id, title, message, posted_by, posted_date
**grievances**: id, student_id, category, description, status
**assignments**: id, title, description, due_date, created_by
**submissions**: id, assignment_id, student_id, submission_text, submitted_date
**admit_cards**: id, student_id, status (Blocked/Released)
**fees**: id, student_id, total_amount, paid_amount, status

## Project Structure

```
src/main/java/com/stark/exam/
├── db/DBConnection.java (Singleton)
├── model/User.java
├── ui/
│   ├── login/LoginController.java
│   ├── student/ (8 controllers)
│   ├── teacher/ (7 controllers)
│   └── author/ (8 controllers)
├── util/
│   ├── PdfService.java
│   └── QrService.java
└── App.java (Main)
```

## Key Technical Implementations

### Database Connection (Singleton)
```java
public class DBConnection {
    private static Connection connection;
    public static Connection getConnection() {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }
}
```

### Anti-Cheating (Focus Loss Detection)
```java
private void setupSecurityListener() {
    parentStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
        if (!isNowFocused && !isSubmitted) {
            warningCount++;
            if (warningCount >= 3) {
                terminateExam();
            } else {
                showAlert("WARNING " + warningCount + "/3: Tab switching detected!");
            }
        }
    });
}
```

### QR Code Generation
```java
public static Image generateQr(String text, int width, int height) {
    QRCodeWriter writer = new QRCodeWriter();
    BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height);
    BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
    return SwingFXUtils.toFXImage(bufferedImage, null);
}
```

### PDF Generation
```java
public static void generateAdmitCard(User user, String filePath) {
    Document document = new Document();
    PdfWriter.getInstance(document, new FileOutputStream(filePath));
    document.open();
    // Add content: header, student details, instructions, footer
    document.close();
}
```

### Reflection-based User Passing
```java
Object controller = loader.getController();
controller.getClass().getMethod("setUser", User.class).invoke(controller, user);
```

## Maven Dependencies

Include: javafx-controls, javafx-fxml, javafx-swing, mysql-connector-j, zxing (core + javase), gson, itextpdf

## UI Design

- **Login**: Clean centered layout with username/password
- **Dashboards**: Grid-based menu with icon buttons, role-specific colors
- **Exam Interface**: Full-screen, timer, question display, navigation
- **Forms**: Consistent labeling, validation, alerts

## Security

- Use PreparedStatement for all SQL queries
- Role-based access control
- Session management via User object
- Focus loss detection for exam integrity

## Deliverables

1. Complete source code with all controllers
2. FXML files for all screens
3. Database schema SQL script
4. Maven pom.xml with all dependencies
5. README with setup instructions
6. Demo launcher for testing (opens 3 windows)

Build a production-ready system that can be deployed as an executable JAR. Ensure clean code, proper error handling, and comprehensive testing.
