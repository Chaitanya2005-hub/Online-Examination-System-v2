-- PostgreSQL Database Schema & Mock Data for Neon Database

-- Drop existing tables if re-initialising
DROP TABLE IF EXISTS submissions CASCADE;
DROP TABLE IF EXISTS assignments CASCADE;
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS results CASCADE;
DROP TABLE IF EXISTS exams CASCADE;
DROP TABLE IF EXISTS subjects CASCADE;
DROP TABLE IF EXISTS admit_cards CASCADE;
DROP TABLE IF EXISTS attendance CASCADE;
DROP TABLE IF EXISTS fees CASCADE;
DROP TABLE IF EXISTS grievances CASCADE;
DROP TABLE IF EXISTS notices CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS live_codes CASCADE;

-- 1. Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL CHECK (role IN ('STUDENT', 'TEACHER', 'ADMIN')),
    erp_id VARCHAR(255),
    student_year INTEGER,
    department VARCHAR(255),
    section VARCHAR(255),
    photo_path VARCHAR(255),
    theme_preference VARCHAR(255)
);

-- 2. Subjects Table
CREATE TABLE subjects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) UNIQUE NOT NULL,
    department VARCHAR(255)
);

-- 3. Exams Table
CREATE TABLE exams (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    exam_date DATE,
    start_time TIME,
    duration_minutes INTEGER,
    status VARCHAR(255) CHECK (status IN ('SCHEDULED', 'ONGOING', 'COMPLETED')),
    subject_id BIGINT REFERENCES subjects(id) ON DELETE CASCADE
);

-- 4. Questions Table
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    exam_id BIGINT NOT NULL REFERENCES exams(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255),
    option_b VARCHAR(255),
    option_c VARCHAR(255),
    option_d VARCHAR(255),
    correct_answer VARCHAR(255)
);

-- 5. Admit Cards Table
CREATE TABLE admit_cards (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(255) NOT NULL CHECK (status IN ('BLOCKED', 'RELEASED'))
);

-- 6. Assignments Table
CREATE TABLE assignments (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date DATE,
    created_by BIGINT REFERENCES users(id) ON DELETE SET NULL
);

-- 7. Submissions Table
CREATE TABLE submissions (
    id BIGSERIAL PRIMARY KEY,
    assignment_id BIGINT NOT NULL REFERENCES assignments(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    submission_text TEXT,
    submitted_date TIMESTAMP
);

-- 8. Attendance Table
CREATE TABLE attendance (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    status VARCHAR(255) NOT NULL CHECK (status IN ('PRESENT', 'ABSENT')),
    marked_by BIGINT REFERENCES users(id) ON DELETE SET NULL
);

-- 9. Fees Table
CREATE TABLE fees (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_amount NUMERIC(38,2) NOT NULL,
    paid_amount NUMERIC(38,2),
    status VARCHAR(255) NOT NULL CHECK (status IN ('PENDING', 'PAID', 'PARTIAL'))
);

-- 10. Grievances Table
CREATE TABLE grievances (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category VARCHAR(255),
    description TEXT,
    status VARCHAR(255) CHECK (status IN ('PENDING', 'RESOLVED'))
);

-- 11. Notices Table
CREATE TABLE notices (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    posted_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
    posted_date TIMESTAMP
);

-- 12. Results Table
CREATE TABLE results (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exam_id BIGINT NOT NULL REFERENCES exams(id) ON DELETE CASCADE,
    score INTEGER,
    total_marks INTEGER,
    status VARCHAR(255),
    security_warnings INTEGER
);

-- 13. Live Codes Table
CREATE TABLE live_codes (
    code VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP,
    expires_at TIMESTAMP
);


-- ==========================================
-- MOCK DATA INSERT STATEMENTS
-- Passwords below are set to: password123 (BCrypt Hash: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y)
-- ==========================================

-- Insert Users
INSERT INTO users (username, password, full_name, role, erp_id, student_year, department, section, theme_preference) VALUES
('stark', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Tony Stark (Admin)', 'ADMIN', 'ADM001', NULL, NULL, NULL, 'dark'),
('teacher', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Dr. Sarah Smith', 'TEACHER', 'FAC001', NULL, 'CSE', NULL, 'dark'),
('241801120002', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'K. Sri Chaitanya', 'STUDENT', '241801120002', 2, 'CSE', 'C', 'dark'),
('241801370001', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Pathivada Nishitha Sai', 'STUDENT', '241801370001', 2, 'AIML', 'A', 'dark'),
('241801370002', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Shaik Subhani', 'STUDENT', '241801370002', 2, 'AIML', 'A', 'dark'),
('241801370003', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Gudivada Vamsi', 'STUDENT', '241801370003', 2, 'AIML', 'A', 'dark'),
('241801370004', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Netheti Tejesh', 'STUDENT', '241801370004', 2, 'AIML', 'A', 'dark'),
('241801350003', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Rallapati Pavan Kumar', 'STUDENT', '241801350003', 2, 'CN', 'B', 'dark'),
('241801380001', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'A. Rama Lakshmi', 'STUDENT', '241801380001', 2, 'CSD', 'B', 'dark'),
('241801390001', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Saripilli Kavya', 'STUDENT', '241801390001', 2, 'CIC', 'B', 'dark'),
('241801120001', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'T. Siddeswar', 'STUDENT', '241801120001', 2, 'CSE', 'C', 'dark'),
('241801340001', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Pathivada Haritha', 'STUDENT', '241801340001', 2, 'CSW', 'C', 'dark'),
('241801360001', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Lenka Surekha', 'STUDENT', '241801360001', 2, 'CSBS', 'C', 'dark'),
('241814100001', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymY0odAn08zo.c4O2/yG3y', 'Sania Nazeer', 'STUDENT', '241814100001', 2, 'BCA', 'C', 'dark');

-- Insert Subjects
INSERT INTO subjects (name, code, department) VALUES
('Java Programming', 'CS301', 'CSE'),
('Advanced Java & Spring Boot', 'CS302', 'CSE'),
('Theory of Computation', 'CS303', 'CSE'),
('Compiler Design', 'CS304', 'CSE'),
('Design and Analysis of Algorithms', 'CS305', 'CSE'),
('Angular Web Development', 'CS306', 'CSE');

-- Insert Exam Timetable
INSERT INTO exams (title, exam_date, start_time, duration_minutes, status, subject_id) VALUES
('Core Java Programming Examination', CURRENT_DATE + INTERVAL '1 day', '10:00:00', 90, 'SCHEDULED', 1),
('Advanced Java & Enterprise Frameworks Exam', CURRENT_DATE + INTERVAL '3 days', '10:00:00', 90, 'SCHEDULED', 2),
('Theory of Computation Final Exam', CURRENT_DATE + INTERVAL '5 days', '14:00:00', 120, 'SCHEDULED', 3),
('Compiler Design Semester Exam', CURRENT_DATE + INTERVAL '7 days', '10:00:00', 120, 'SCHEDULED', 4),
('Design & Analysis of Algorithms Exam', CURRENT_DATE + INTERVAL '9 days', '14:00:00', 120, 'SCHEDULED', 5),
('Angular Frontend Framework Exam', CURRENT_DATE + INTERVAL '11 days', '10:00:00', 90, 'SCHEDULED', 6);

-- Insert Questions for Timetable Exams
INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES
-- Java Programming (Exam 1)
(1, 'Which keyword is used to prevent method overriding in Java?', 'static', 'final', 'abstract', 'private', 'B'),
(1, 'Which memory area in JVM stores class structures and static variables?', 'Heap', 'Stack', 'Method Area / Metaspace', 'Program Counter Register', 'C'),
-- Advanced Java (Exam 2)
(2, 'Which annotation in Spring Boot marks a class as a RESTful Controller?', '@Controller', '@Service', '@RestController', '@Component', 'C'),
(2, 'What is the default scope of a Spring Bean?', 'prototype', 'singleton', 'request', 'session', 'B'),
-- Theory of Computation (Exam 3)
(3, 'Which automaton recognizes Context-Free Languages (CFL)?', 'Finite Automaton', 'Pushdown Automaton (PDA)', 'Linear Bounded Automaton', 'Turing Machine', 'B'),
(3, 'What is the Pumping Lemma used for?', 'To prove a language is regular', 'To prove a language is NOT regular', 'To minimize a DFA', 'To parse CFG', 'B'),
-- Compiler Design (Exam 4)
(4, 'Which phase of a compiler performs syntax analysis?', 'Lexical Analyzer', 'Parser', 'Semantic Analyzer', 'Code Generator', 'B'),
(4, 'What data structure is typically used for symbol table management in compilers?', 'Stack', 'Queue', 'Hash Table', 'Binary Tree', 'C'),
-- DAA (Exam 5)
(5, 'Which algorithmic technique solves subproblems only once and stores their results?', 'Greedy Method', 'Divide & Conquer', 'Dynamic Programming', 'Backtracking', 'C'),
(5, 'What is the average-case time complexity of Merge Sort?', 'O(N²)', 'O(N log N)', 'O(N)', 'O(log N)', 'B'),
-- Angular (Exam 6)
(6, 'Which decorator is used to define an Angular Component?', '@NgModule', '@Component', '@Injectable', '@Directive', 'B'),
(6, 'Which command CLI command generates a new service in Angular?', 'ng new service', 'ng generate service', 'ng create service', 'ng add service', 'B');

-- Insert Admit Card
INSERT INTO admit_cards (student_id, status) VALUES
(3, 'RELEASED'),
(4, 'BLOCKED');

-- Insert Notices
INSERT INTO notices (title, message, posted_by, posted_date) VALUES
('Semester Examination Schedule Released', 'The schedule for the upcoming semester examinations has been published on the portal. Please verify your admit cards.', 1, NOW()),
('Assignment Submission Deadline Reminder', 'All CSE 3rd Year students are instructed to submit their Data Structures assignment by Friday.', 2, NOW());

-- Insert Assignments
INSERT INTO assignments (title, description, due_date, created_by) VALUES
('Binary Search Tree Implementation', 'Implement a BST in Java supporting insertion, deletion, and tree traversals.', CURRENT_DATE + INTERVAL '5 days', 2);

-- Insert Submissions
INSERT INTO submissions (assignment_id, student_id, submission_text, submitted_date) VALUES
(1, 3, 'Public Class BinaryTree { Node root; ... }', NOW());

-- Insert Attendance
INSERT INTO attendance (student_id, date, status, marked_by) VALUES
(3, CURRENT_DATE, 'PRESENT', 2),
(4, CURRENT_DATE, 'ABSENT', 2);

-- Insert Fees
INSERT INTO fees (student_id, total_amount, paid_amount, status) VALUES
(3, 2500.00, 1000.00, 'PARTIAL'),
(4, 2500.00, 2500.00, 'PAID');

-- Insert Grievances
INSERT INTO grievances (student_id, category, description, status) VALUES
(3, 'Admit Card Issue', 'Request to update father name on admit card.', 'PENDING');
