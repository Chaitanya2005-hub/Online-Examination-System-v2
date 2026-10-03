# 🅰️ Master Project Prompt: University Assessment & Mastery Portal (Angular Edition)

## 📌 Project Overview
Build a state-of-the-art, enterprise-grade web application named **"University Assessment and Mastery Portal"** using **Angular 17+ (Standalone Components, Signals, RxJS)** for the frontend and a **RESTful API Service (Spring Boot / Node.js Express)** for the backend. 

The application digitizes academic administration, online examination with live webcam proctoring, AI-assisted question generation, dynamic QR attendance, admit card status tracking, fee verification, and grievance redressal for educational institutions.

---

## 🛠️ Technology Stack Specifications

### 🅰️ Frontend (Angular Framework)
- **Framework**: Angular 17/18 (Standalone Components Architecture)
- **State & Data Flow**: Angular Signals, RxJS Observables, Reactive Forms
- **UI Components & Styling**: Angular Material / TailwindCSS (with Dark/Light Mode Theme Toggle)
- **Routing**: Angular Router with Functional Route Guards (`AuthGuard`, `RoleGuard`)
- **HTTP Layer**: `HttpClient` with Interceptors (`JwtInterceptor`, `ApiErrorInterceptor`)
- **Proctoring**: HTML5 `navigator.mediaDevices.getUserMedia` WebCam API & DOM Focus/Visibility Listeners
- **QR Code Scanning**: `@zxing/ngx-scanner` or `ngx-qrcode`
- **PDF Generation**: `jspdf` & `pdfmake` for client-side PDF document preview & download

### ⚙️ Backend (REST API Architecture)
- **Backend Tech**: Spring Boot 3.x (Java 21) or Node.js NestJS/Express
- **Security**: Spring Security / Passport.js with JWT Authentication (Stateless Sessions)
- **Database**: PostgreSQL (Neon Cloud) / MySQL 8.0+
- **AI Generation**: Google Gemini API (v1beta) integration

---

## 🗂️ Angular Project Directory & Module Architecture

Generate the Angular client structure as follows:

```text
src/app/
├── core/
│   ├── guards/
│   │   ├── auth.guard.ts
│   │   └── role.guard.ts
│   ├── interceptors/
│   │   ├── jwt.interceptor.ts
│   │   └── error.interceptor.ts
│   ├── models/
│   │   ├── user.model.ts
│   │   ├── exam.model.ts
│   │   ├── question.model.ts
│   │   ├── result.model.ts
│   │   ├── attendance.model.ts
│   │   ├── fee.model.ts
│   │   └── admit-card.model.ts
│   └── services/
│       ├── auth.service.ts
│       ├── exam.service.ts
│       ├── proctoring.service.ts
│       ├── attendance.service.ts
│       ├── fee.service.ts
│       ├── admit-card.service.ts
│       ├── gemini-ai.service.ts
│       └── theme.service.ts
├── shared/
│   ├── components/
│   │   ├── navbar/
│   │   ├── footer/
│   │   ├── theme-toggle/
│   │   ├── stat-card/
│   │   └── confirm-modal/
│   └── pipes/
│       └── inr-currency.pipe.ts
└── features/
    ├── auth/
    │   └── login/
    │       └── login.component.ts
    ├── student/
    │   ├── student-layout.component.ts
    │   ├── dashboard/
    │   ├── exam-list/
    │   ├── exam-interface/ (Anti-Cheat 3-Strike Rule & WebCam Proctoring)
    │   ├── attendance/ (QR Scanner / Code Entry)
    │   ├── admit-card/ (Status Check & Hall Ticket PDF Download)
    │   ├── fee-details/ (Payment Status & PDF Receipt Download)
    │   ├── results/
    │   └── grievances/
    ├── teacher/
    │   ├── teacher-layout.component.ts
    │   ├── dashboard/
    │   ├── upload-questions/ (Manual Question Form & Gemini AI Generator Form)
    │   ├── mark-attendance/ (Live Auto-Refreshing QR Code Host)
    │   ├── live-proctoring/ (WebCam Stream Monitoring Dashboard)
    │   └── student-progress/
    └── admin/
        ├── admin-layout.component.ts
        ├── dashboard/
        ├── manage-users/
        ├── schedule-exam/
        ├── manage-admit-cards/ (Block/Release Status Toggle & Badges)
        ├── admin-fees/ (Edit Amount & Approve/Disapprove Controls)
        └── manage-subjects/
```

---

## 🗄️ Database Schema & REST API Specifications

### Database Entities
1. `User`: `id`, `username`, `password`, `fullName`, `role` (`STUDENT`, `TEACHER`, `ADMIN`), `erpId`, `year`, `department`, `section`, `photoPath`
2. `Subject`: `id`, `name`, `code`, `department`
3. `Exam`: `id`, `title`, `subjectId`, `examDate`, `startTime`, `durationMinutes`, `status` (`SCHEDULED`, `ONGOING`, `COMPLETED`)
4. `Question`: `id`, `examId`, `questionText`, `optionA`, `optionB`, `optionC`, `optionD`, `correctAnswer`
5. `Result`: `id`, `studentId`, `examId`, `score`, `totalMarks`, `status`, `securityWarnings`
6. `Attendance`: `id`, `studentId`, `date`, `status` (`PRESENT`, `ABSENT`)
7. `LiveCode`: `id`, `code`, `createdAt`, `expiresAt`
8. `AdmitCard`: `id`, `studentId`, `status` (`BLOCKED`, `RELEASED`)
9. `Fee`: `id`, `studentId`, `totalAmount`, `paidAmount`, `status` (`PENDING`, `PARTIAL`, `PAID`), `approvalStatus` (`PENDING`, `APPROVED`, `DISAPPROVED`)
10. `Notice`: `id`, `title`, `message`, `postedBy`, `postedDate`
11. `Grievance`: `id`, `studentId`, `category`, `description`, `status` (`PENDING`, `RESOLVED`)

### Primary REST API Endpoints
- **Auth**: `POST /api/auth/login`, `GET /api/auth/me`
- **Exams**: `GET /api/exams`, `POST /api/exams/schedule`, `GET /api/exams/:id/questions`, `POST /api/exams/:id/submit`
- **Gemini AI**: `POST /api/teacher/auto-generate-questions`
- **Attendance**: `GET /api/teacher/qr-code` (PNG byte stream), `POST /api/student/attendance/mark`
- **Admit Cards**: `GET /api/admin/admit-cards`, `POST /api/admin/admit-cards/:studentId/status`
- **Fees**: `GET /api/fees/my-status`, `POST /api/admin/fees/edit`, `POST /api/admin/fees/:id/approve`

---

## 🔑 Key Operational & Implementation Rules

### 1. 🛡️ Anti-Cheat & Proctoring Engine (`exam-interface.component.ts`)
- Attach Angular `@HostListener('window:blur')` and `document.addEventListener('visibilitychange')`.
- Maintain a `warningCount` Signal/variable:
  - **1st Tab Switch**: Display a modal dialog ("Warning: Do not switch tabs during examination").
  - **2nd Tab Switch**: Display a high-priority alert.
  - **3rd Tab Switch**: Force submit the current answers and record security violations in `Result.securityWarnings`.
- Stream webcam feed locally using `navigator.mediaDevices.getUserMedia({ video: true })` and render inside a proctoring thumbnail container.

### 2. 🤖 Gemini AI Question Generator (`upload-questions.component.ts`)
- Form inputs: `examId`, `count`, `topicFocus`, `difficultyLevel`.
- Send request to backend `/api/teacher/auto-generate-questions`.
- If Gemini API key is missing on the server, gracefully generate topic-tailored fallback questions so the UI never crashes.

### 3. 📱 Dynamic Attendance QR Code Host (`mark-attendance.component.ts`)
- Teacher component polls `/api/teacher/qr-code` every 10 seconds.
- Displays live QR image alongside the 4-digit code.
- Expiration is set to 60 seconds on the server.

### 4. 📄 Official Admit Card & Fee Receipt PDF Generation
- In `admit-card.component.ts`, check `admitCard.status`:
  - If `BLOCKED`: Show a red badge and disable download.
  - If `RELEASED`: Show a green badge and render printable PDF using `jspdf` / `pdfmake`.

### 5. 🎨 Theme System
- Implement a `ThemeService` using Angular Signals. Toggle `.dark-mode` class on `document.documentElement` and persist choice in `localStorage`.
