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
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final NoticeRepository noticeRepository;
    private final AssignmentRepository assignmentRepository;
    private final FeeRepository feeRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername("241801370001")) {
            String encodedPass = passwordEncoder.encode("123");

            // Seed Admin
            User admin = new User();
            admin.setUsername("stark");
            admin.setPassword(encodedPass);
            admin.setFullName("Tony Stark (Admin)");
            admin.setRole(User.Role.ADMIN);
            admin.setErpId("ADM001");
            userRepository.save(admin);

            // Seed Faculty / Teachers
            User teacher = new User();
            teacher.setUsername("bruce");
            teacher.setPassword(encodedPass);
            teacher.setFullName("Prof. Bruce Banner");
            teacher.setRole(User.Role.TEACHER);
            teacher.setDepartment("CSE");
            teacher.setErpId("FAC001");
            userRepository.save(teacher);

            User teacher2 = new User();
            teacher2.setUsername("faculty001");
            teacher2.setPassword(encodedPass);
            teacher2.setFullName("Dr. Ramesh Kumar");
            teacher2.setRole(User.Role.TEACHER);
            teacher2.setDepartment("CSE");
            teacher2.setErpId("FAC1001");
            userRepository.save(teacher2);

            // Seed Main Student (K. Sri Chaitanya)
            User mainStudent = new User();
            mainStudent.setUsername("241801120002");
            mainStudent.setPassword(encodedPass);
            mainStudent.setFullName("K. Sri Chaitanya");
            mainStudent.setRole(User.Role.STUDENT);
            mainStudent.setDepartment("CSE");
            mainStudent.setYear(2);
            mainStudent.setSection("C");
            mainStudent.setErpId("241801120002");
            User student = userRepository.save(mainStudent);

            // Seed Additional Students from Database Dump
            String[][] studentData = {
                {"241801370001", "Pathivada Nishitha Sai", "AIML", "2", "A"},
                {"241801370002", "Shaik Subhani", "AIML", "2", "A"},
                {"241801370003", "Gudivada Vamsi", "AIML", "2", "A"},
                {"241801370004", "Netheti Tejesh", "AIML", "2", "A"},
                {"241801370005", "Kakinada Dhanunjaya Rao", "AIML", "2", "A"},
                {"241801370008", "Nalla Prasad", "AIML", "2", "A"},
                {"241801370009", "Yerra Niharika", "AIML", "2", "A"},
                {"241801370010", "Paila Surya Karthik", "AIML", "2", "A"},
                {"241801370011", "Pydi Vamsi Kumar", "AIML", "2", "A"},
                {"241801370012", "JAKKAMSETTY R V RISHITHA SRI", "AIML", "2", "A"},
                {"241801350003", "Rallapati Pavan Kumar", "CN", "2", "B"},
                {"241801350004", "Pulipati Aiswarnya", "CN", "2", "B"},
                {"241801380001", "A. Rama Lakshmi", "CSD", "2", "B"},
                {"241801380002", "Jaggurothu Gayatri", "CSD", "2", "B"},
                {"241801390001", "Saripilli Kavya", "CIC", "2", "B"},
                {"241801120001", "T. Siddeswar", "CSE", "2", "C"},
                {"241801120004", "Nalla Prasanth", "CSE", "2", "C"},
                {"241801120005", "Gandi Lokesh", "CSE", "2", "C"},
                {"241801340001", "Pathivada Haritha", "CSW", "2", "C"},
                {"241801360001", "Lenka Surekha", "CSBS", "2", "C"},
                {"241814100001", "Sania Nazeer", "BCA", "2", "C"}
            };

            for (String[] sData : studentData) {
                User s = new User();
                s.setUsername(sData[0]);
                s.setPassword(encodedPass);
                s.setFullName(sData[1]);
                s.setRole(User.Role.STUDENT);
                s.setDepartment(sData[2]);
                s.setYear(Integer.parseInt(sData[3]));
                s.setSection(sData[4]);
                s.setErpId(sData[0]);
                userRepository.save(s);
            }

            // Seed Subjects
            Subject sJava = new Subject(null, "Java Programming", "CS301", "CSE");
            Subject sAdvJava = new Subject(null, "Advanced Java & Spring Boot", "CS302", "CSE");
            Subject sToc = new Subject(null, "Theory of Computation", "CS303", "CSE");
            Subject sCompiler = new Subject(null, "Compiler Design", "CS304", "CSE");
            Subject sDaa = new Subject(null, "Design and Analysis of Algorithms", "CS305", "CSE");
            Subject sAngular = new Subject(null, "Angular Web Development", "CS306", "CSE");

            subjectRepository.save(sJava);
            subjectRepository.save(sAdvJava);
            subjectRepository.save(sToc);
            subjectRepository.save(sCompiler);
            subjectRepository.save(sDaa);
            subjectRepository.save(sAngular);

            // Seed Notice
            Notice notice = new Notice();
            notice.setTitle("Semester Examination Schedule Released");
            notice.setMessage("The official exam timetable for Theory of Computing, Compiler Design, DAA, Angular, Java, and Advance Java is now live. Please review your schedules.");
            notice.setPostedBy(admin);
            notice.setPostedDate(LocalDateTime.now());
            noticeRepository.save(notice);

            // Seed Assignment
            Assignment assignment = new Assignment();
            assignment.setTitle("Algorithm Complexity & Compiler Parsing");
            assignment.setDescription("Solve parsing table problems and analyze QuickSort space complexity.");
            assignment.setDueDate(LocalDate.now().plusDays(7));
            assignment.setCreatedBy(teacher);
            assignmentRepository.save(assignment);

            // Seed Fee Mock Data for all students
            List<User> allStudents = userRepository.findByRole(User.Role.STUDENT);
            int feeIndex = 0;
            for (User st : allStudents) {
                Fee f = new Fee();
                f.setStudent(st);
                
                if (st.getUsername().equals("241801120002")) {
                    // Main Student
                    f.setTotalAmount(new BigDecimal("50000.00"));
                    f.setPaidAmount(new BigDecimal("35000.00"));
                    f.setStatus(Fee.FeeStatus.PARTIAL);
                    f.setApprovalStatus(Fee.ApprovalStatus.APPROVED);
                } else if (feeIndex % 4 == 0) {
                    f.setTotalAmount(new BigDecimal("50000.00"));
                    f.setPaidAmount(new BigDecimal("50000.00"));
                    f.setStatus(Fee.FeeStatus.PAID);
                    f.setApprovalStatus(Fee.ApprovalStatus.APPROVED);
                } else if (feeIndex % 4 == 1) {
                    f.setTotalAmount(new BigDecimal("52000.00"));
                    f.setPaidAmount(new BigDecimal("25000.00"));
                    f.setStatus(Fee.FeeStatus.PARTIAL);
                    f.setApprovalStatus(Fee.ApprovalStatus.APPROVED);
                } else if (feeIndex % 4 == 2) {
                    f.setTotalAmount(new BigDecimal("48000.00"));
                    f.setPaidAmount(new BigDecimal("10000.00"));
                    f.setStatus(Fee.FeeStatus.PARTIAL);
                    f.setApprovalStatus(Fee.ApprovalStatus.PENDING);
                } else {
                    f.setTotalAmount(new BigDecimal("50000.00"));
                    f.setPaidAmount(new BigDecimal("0.00"));
                    f.setStatus(Fee.FeeStatus.PENDING);
                    f.setApprovalStatus(Fee.ApprovalStatus.DISAPPROVED);
                }
                feeRepository.save(f);
                feeIndex++;
            }

            // ==========================================
            // SEED EXAM TIMETABLE & QUESTION PAPERS
            // ==========================================

            // 1. Core Java Exam (10 Questions)
            Exam e1 = examRepository.save(new Exam(null, "Core Java Programming Examination", LocalDate.now().plusDays(1), LocalTime.of(10, 0), 90, Exam.ExamStatus.SCHEDULED, sJava));
            questionRepository.save(new Question(null, e1, "Which keyword is used to prevent method overriding in Java?", "static", "final", "abstract", "private", "B"));
            questionRepository.save(new Question(null, e1, "Which memory area in JVM stores class structures and static variables?", "Heap", "Stack", "Method Area / Metaspace", "Program Counter Register", "C"));
            questionRepository.save(new Question(null, e1, "Which collection class allows null keys and null values in Java?", "HashMap", "TreeMap", "Hashtable", "ArrayDeque", "A"));
            questionRepository.save(new Question(null, e1, "What is the root superclass of all classes in Java?", "System", "Class", "Object", "Base", "C"));
            questionRepository.save(new Question(null, e1, "Which interface must a class implement to define code executable by a Thread?", "Callable", "Runnable", "Serializable", "Cloneable", "B"));
            questionRepository.save(new Question(null, e1, "Which method is used to compare string character content in Java?", "==", "compareToIgnoreCase()", ".equals()", "matches()", "C"));
            questionRepository.save(new Question(null, e1, "What happens if a try block executes without throwing an exception?", "finally block is skipped", "finally block always executes", "catch block executes", "JVM crashes", "B"));
            questionRepository.save(new Question(null, e1, "Which modifier makes a variable shared across all instances of a class?", "volatile", "static", "transient", "synchronized", "B"));
            questionRepository.save(new Question(null, e1, "Which primitive data type consumes 8 bytes of memory in Java?", "int", "float", "char", "double", "D"));
            questionRepository.save(new Question(null, e1, "Which feature introduced in Java 8 allows passing lambda functions as parameters?", "Generics", "Annotations", "Lambda Expressions", "Reflection API", "C"));

            // 2. Advanced Java & Spring Boot Exam (10 Questions)
            Exam e2 = examRepository.save(new Exam(null, "Advanced Java & Enterprise Frameworks Exam", LocalDate.now().plusDays(3), LocalTime.of(10, 0), 90, Exam.ExamStatus.SCHEDULED, sAdvJava));
            questionRepository.save(new Question(null, e2, "Which annotation in Spring Boot marks a class as a RESTful Controller?", "@Controller", "@Service", "@RestController", "@Component", "C"));
            questionRepository.save(new Question(null, e2, "What is the default scope of a Spring Bean in Spring Container?", "prototype", "singleton", "request", "session", "B"));
            questionRepository.save(new Question(null, e2, "Which annotation maps HTTP POST requests in Spring MVC Controllers?", "@PostMapping", "@GetMapping", "@PutMapping", "@RequestMapping", "A"));
            questionRepository.save(new Question(null, e2, "Which component handles object-relational mapping (ORM) in Spring Data JPA?", "JDBC Template", "MyBatis", "Hibernate", "Spring Security", "C"));
            questionRepository.save(new Question(null, e2, "Which file configures server port and datasource properties in Spring Boot?", "pom.xml", "application.properties", "web.xml", "context.xml", "B"));
            questionRepository.save(new Question(null, e2, "Which annotation is used for automatic Dependency Injection in Spring?", "@Inject", "@Autowired", "@Resource", "@Bean", "B"));
            questionRepository.save(new Question(null, e2, "What HTTP status code represents a successful request (OK)?", "200", "404", "500", "302", "A"));
            questionRepository.save(new Question(null, e2, "Which JPA annotation specifies the primary key of an entity?", "@Id", "@Column", "@GeneratedValue", "@Entity", "A"));
            questionRepository.save(new Question(null, e2, "Which annotation enables auto-configuration and component scanning in Spring Boot?", "@Configuration", "@ComponentScan", "@SpringBootApplication", "@EnableAutoConfiguration", "C"));
            questionRepository.save(new Question(null, e2, "Which embedded servlet container is included by default in Spring Boot Web?", "Apache Tomcat", "Eclipse Jetty", "WildFly", "GlassFish", "A"));

            // 3. Theory of Computation (TOC) Exam (10 Questions)
            Exam e3 = examRepository.save(new Exam(null, "Theory of Computation Final Exam", LocalDate.now().plusDays(5), LocalTime.of(14, 0), 120, Exam.ExamStatus.SCHEDULED, sToc));
            questionRepository.save(new Question(null, e3, "Which type of automaton recognizes Context-Free Languages (CFL)?", "Finite Automaton (FA)", "Pushdown Automaton (PDA)", "Linear Bounded Automaton", "Turing Machine", "B"));
            questionRepository.save(new Question(null, e3, "What is the primary purpose of applying Pumping Lemma in formal language theory?", "To minimize a DFA", "To prove a language is NOT regular", "To convert NFA to DFA", "To generate parser tables", "B"));
            questionRepository.save(new Question(null, e3, "Which grammar classification in Chomsky Hierarchy corresponds to Regular Languages?", "Type 0", "Type 1", "Type 2", "Type 3", "D"));
            questionRepository.save(new Question(null, e3, "Which computational model is equivalent to an Unrestricted Grammar (Type 0)?", "Pushdown Automaton", "Finite State Machine", "Turing Machine", "Mealy Machine", "C"));
            questionRepository.save(new Question(null, e3, "The union of two Regular Languages is always which of the following?", "Regular Language", "Context-Free Language", "Non-Deterministic Language", "Recursive Enumerable", "A"));
            questionRepository.save(new Question(null, e3, "Can a Deterministic Finite Automaton (DFA) have multiple initial start states?", "Yes, up to N states", "No, exactly one start state", "Yes, if epsilon is present", "Only in non-deterministic mode", "B"));
            questionRepository.save(new Question(null, e3, "What is the algorithm used to reduce the number of redundant states in a DFA?", "State Reduction", "NFA Conversion", "DFA Minimization (Hopcroft)", "Pumping Method", "C"));
            questionRepository.save(new Question(null, e3, "Which language cannot be recognized by any Finite State Automaton?", "L = { a^n | n >= 0 }", "L = { a^n b^n | n >= 0 }", "L = { a, b, c }", "L = { (ab)^n }", "B"));
            questionRepository.save(new Question(null, e3, "Is the Halting Problem for Turing Machines decidable or undecidable?", "Decidable in O(N)", "Undecidable", "Polynomial Time Decidable", "Context-Sensitive Decidable", "B"));
            questionRepository.save(new Question(null, e3, "What transition in an NFA allows state transitions without consuming input symbols?", "Loop Transition", "Final Transition", "Epsilon / Null Transition", "Universal Transition", "C"));

            // 4. Compiler Design Exam (10 Questions)
            Exam e4 = examRepository.save(new Exam(null, "Compiler Design Semester Exam", LocalDate.now().plusDays(7), LocalTime.of(10, 0), 120, Exam.ExamStatus.SCHEDULED, sCompiler));
            questionRepository.save(new Question(null, e4, "Which phase of a compiler performs syntax analysis and produces a Parse Tree?", "Lexical Analyzer", "Parser", "Semantic Analyzer", "Code Generator", "B"));
            questionRepository.save(new Question(null, e4, "What data structure is standard for symbol table management in modern compilers?", "Stack", "Queue", "Hash Table", "Binary Search Tree", "C"));
            questionRepository.save(new Question(null, e4, "Which compiler phase converts raw source code into a sequence of tokens?", "Lexical Analyzer / Scanner", "Parser", "Intermediate Code Generator", "Optimizer", "A"));
            questionRepository.save(new Question(null, e4, "What does DAG stand for in compiler code optimization techniques?", "Direct Allocation Graph", "Directed Acyclic Graph", "Dynamic Analysis Group", "Data Access Gate", "B"));
            questionRepository.save(new Question(null, e4, "Which parsing algorithm constructs parse trees top-down starting from root?", "LL(1) Top-Down Parser", "LR(1) Bottom-Up Parser", "LALR Parser", "SLR Parser", "A"));
            questionRepository.save(new Question(null, e4, "What is the process of removing redundant or dead code without altering program logic?", "Code Parsing", "Lexical Scanning", "Code Optimization", "Assembly Generation", "C"));
            questionRepository.save(new Question(null, e4, "Which intermediate representation format uses at most three address operands per instruction?", "Single Address Code", "Three-Address Code (TAC)", "Quadruple Bytecode", "Postfix Expression", "B"));
            questionRepository.save(new Question(null, e4, "What type of error is detected when variable scope rules or type mismatches occur?", "Lexical Error", "Semantic Error", "Syntax Error", "Linker Error", "B"));
            questionRepository.save(new Question(null, e4, "Which bottom-up parsing technique is used by compiler tools like YACC and Bison?", "LL(0)", "Recursive Descent", "LALR(1) Parser", "Operator Precedence", "C"));
            questionRepository.save(new Question(null, e4, "Which component of a compiler generates assembly code or machine binary code?", "Preprocessor", "Linker", "Loader", "Code Generator", "D"));

            // 5. Design & Analysis of Algorithms (DAA - 10 Questions)
            Exam e5 = examRepository.save(new Exam(null, "Design & Analysis of Algorithms Exam", LocalDate.now().plusDays(9), LocalTime.of(14, 0), 120, Exam.ExamStatus.SCHEDULED, sDaa));
            questionRepository.save(new Question(null, e5, "Which algorithmic paradigm solves subproblems only once and stores results in a table?", "Greedy Approach", "Divide and Conquer", "Dynamic Programming", "Backtracking", "C"));
            questionRepository.save(new Question(null, e5, "What is the average-case time complexity of the Merge Sort algorithm?", "O(N²)", "O(N log N)", "O(N)", "O(log N)", "B"));
            questionRepository.save(new Question(null, e5, "Which algorithm finds the single-source shortest paths in a non-negative weighted graph?", "Dijkstra Algorithm", "Kruskal Algorithm", "Prim Algorithm", "Floyd-Warshall", "A"));
            questionRepository.save(new Question(null, e5, "What is the worst-case time complexity of QuickSort algorithm?", "O(N log N)", "O(N²)", "O(N)", "O(2^N)", "B"));
            questionRepository.save(new Question(null, e5, "Which greedy algorithm builds a Minimum Spanning Tree by sorting graph edges?", "Floyd Algorithm", "Kruskal Algorithm", "Bellman-Ford", "Depth-First Search", "B"));
            questionRepository.save(new Question(null, e5, "Which complexity class contains decision problems solvable in polynomial time by deterministic Turing Machine?", "Class P", "Class NP", "NP-Complete", "NP-Hard", "A"));
            questionRepository.save(new Question(null, e5, "Which algorithm strategy is utilized in Binary Search?", "Greedy Method", "Dynamic Programming", "Divide and Conquer", "Branch and Bound", "C"));
            questionRepository.save(new Question(null, e5, "What is the expected average-case time complexity for search operations in a Hash Table?", "O(1)", "O(log N)", "O(N)", "O(N log N)", "A"));
            questionRepository.save(new Question(null, e5, "Which problem is a classic example solved using Backtracking technique?", "Fractional Knapsack", "N-Queens Problem", "Huffman Coding", "Job Sequencing", "B"));
            questionRepository.save(new Question(null, e5, "Which property must hold for a problem to be solvable via Greedy Algorithm?", "Optimal Substructure & Greedy Choice Property", "Overlapping Subproblems", "Recursive Invariance", "State Minimization", "A"));

            // 6. Angular Web Development Exam (10 Questions)
            Exam e6 = examRepository.save(new Exam(null, "Angular Frontend Framework Exam", LocalDate.now().plusDays(11), LocalTime.of(10, 0), 90, Exam.ExamStatus.SCHEDULED, sAngular));
            questionRepository.save(new Question(null, e6, "Which TypeScript decorator is used to define an Angular Component?", "@NgModule", "@Component", "@Injectable", "@Directive", "B"));
            questionRepository.save(new Question(null, e6, "Which Angular CLI command is used to generate a new service class?", "ng new service", "ng generate service <name>", "ng create service", "ng add service", "B"));
            questionRepository.save(new Question(null, e6, "Which Angular module provides HttpClient service for executing HTTP REST requests?", "FormsModule", "BrowserModule", "HttpClientModule", "RouterModule", "C"));
            questionRepository.save(new Question(null, e6, "What template syntax is used for Two-Way Data Binding in Angular forms?", "[(ngModel)]", "{{ngModel}}", "[ngModel]", "(ngModel)", "A"));
            questionRepository.save(new Question(null, e6, "Which Lifecycle Hook executes after Angular initializes component input properties?", "ngOnChanges", "ngOnInit", "ngDoCheck", "ngAfterViewInit", "B"));
            questionRepository.save(new Question(null, e6, "Which RxJS object represents an asynchronous data stream handled in Angular apps?", "Promise", "Event", "Observable", "Callback", "C"));
            questionRepository.save(new Question(null, e6, "Which structural directive conditionally renders or removes DOM elements in template?", "*ngIf", "*ngFor", "[ngClass]", "[ngStyle]", "A"));
            questionRepository.save(new Question(null, e6, "Which service class is used to programmatically navigate between Angular routes?", "Location", "Router", "ActivatedRoute", "NavigationEnd", "B"));
            questionRepository.save(new Question(null, e6, "Which property in @Component decorator specifies external HTML template path?", "template", "styles", "templateUrl", "viewPath", "C"));
            questionRepository.save(new Question(null, e6, "Which decorator allows Angular to inject dependencies into a service class?", "@Injectable", "@Inject", "@Component", "@ProvidedIn", "A"));

            System.out.println("DataInitializer: 60 Questions across 6 Exams successfully seeded!");
        }
    }
}
