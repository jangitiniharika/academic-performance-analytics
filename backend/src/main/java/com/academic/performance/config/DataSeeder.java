package com.academic.performance.config;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.ResultRequest;
import com.academic.performance.entity.Assignment;
import com.academic.performance.entity.AssignmentSubmission;
import com.academic.performance.entity.Attendance;
import com.academic.performance.entity.AttendanceStatus;
import com.academic.performance.entity.Department;
import com.academic.performance.entity.Exam;
import com.academic.performance.entity.Faculty;
import com.academic.performance.entity.Marks;
import com.academic.performance.entity.Role;
import com.academic.performance.entity.Student;
import com.academic.performance.entity.Subject;
import com.academic.performance.entity.SubmissionStatus;
import com.academic.performance.entity.User;
import com.academic.performance.repository.AcademicResultRepository;
import com.academic.performance.repository.AssignmentRepository;
import com.academic.performance.repository.AssignmentSubmissionRepository;
import com.academic.performance.repository.AttendanceRepository;
import com.academic.performance.repository.DepartmentRepository;
import com.academic.performance.repository.ExamRepository;
import com.academic.performance.repository.FacultyRepository;
import com.academic.performance.repository.MarksRepository;
import com.academic.performance.repository.PerformanceAlertRepository;
import com.academic.performance.repository.PerformancePredictionRepository;
import com.academic.performance.repository.StudentRepository;
import com.academic.performance.repository.SubjectRepository;
import com.academic.performance.repository.UserRepository;
import com.academic.performance.service.AlertService;
import com.academic.performance.service.ResultService;

/**
 * Creates a realistic, varied demo data set for local development:
 * <ul>
 *   <li>three login accounts (Admin, Faculty, Student);</li>
 *   <li>one department and one faculty member who teaches every subject;</li>
 *   <li>six subjects (three "current", three "previous");</li>
 *   <li>eleven students with mixed performance levels (strong / average / weak);</li>
 *   <li>attendance, internal marks, previous-semester results/CGPA, assignments
 *       with pending/submitted/late/graded submissions, and the early-warning
 *       alerts that follow from those records.</li>
 * </ul>
 *
 * <p>Every step is <strong>idempotent</strong>: rows are looked up by their
 * natural key first and only created when missing, so the seeder can run on every
 * start-up without duplicating or overwriting existing records. The alert pass
 * runs only once (when no alert exists yet) because alert generation also
 * persists a prediction. Passwords are BCrypt-hashed before storage; the plain
 * text only lives in configuration and the README.
 *
 * <p>It never deletes data, and is disabled in tests
 * ({@code app.seed.enabled=false}) and can be switched off locally with
 * {@code APP_SEED_ENABLED=false}. It must never be enabled in production; point
 * a throwaway development database at it instead.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    /** Attendance sessions recorded per current subject and student. */
    private static final int SESSION_COUNT = 15;

    /**
     * A demo student's performance band. The numbers are deliberately spread so
     * the analytics, prediction and alert rules have strong, borderline and
     * failing students to work with.
     */
    private enum Tier {
        STRONG(94, 86, 9.0),
        AVERAGE(80, 68, 7.2),
        WEAK(55, 42, 5.0);

        private final int attendancePct;
        private final int marksBase;
        private final double gradeBase;

        Tier(int attendancePct, int marksBase, double gradeBase) {
            this.attendancePct = attendancePct;
            this.marksBase = marksBase;
            this.gradeBase = gradeBase;
        }
    }

    /** A demo student that has no login account (only an Admin/Faculty view). */
    private record Roster(String rollNumber, String firstName, String lastName, Tier tier) {
    }

    private static final List<Roster> ROSTER = List.of(
            new Roster("CS2023002", "Aarav", "Sharma", Tier.STRONG),
            new Roster("CS2023003", "Vivaan", "Reddy", Tier.STRONG),
            new Roster("CS2023004", "Ananya", "Iyer", Tier.STRONG),
            new Roster("CS2023005", "Diya", "Nair", Tier.AVERAGE),
            new Roster("CS2023006", "Aditya", "Menon", Tier.AVERAGE),
            new Roster("CS2023007", "Ishita", "Rao", Tier.AVERAGE),
            new Roster("CS2023008", "Kabir", "Verma", Tier.WEAK),
            new Roster("CS2023009", "Meera", "Krishnan", Tier.WEAK),
            new Roster("CS2023010", "Rohan", "Gupta", Tier.WEAK),
            new Roster("CS2023011", "Saanvi", "Patel", Tier.AVERAGE));

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final AcademicResultRepository resultRepository;
    private final PerformanceAlertRepository alertRepository;
    private final PerformancePredictionRepository predictionRepository;
    private final ResultService resultService;
    private final AlertService alertService;
    private final PasswordEncoder passwordEncoder;

    private final String departmentName;
    private final String departmentCode;

    private final String adminUsername;
    private final String adminEmail;
    private final String adminPassword;

    private final String facultyUsername;
    private final String facultyEmail;
    private final String facultyPassword;

    private final String studentUsername;
    private final String studentEmail;
    private final String studentPassword;

    public DataSeeder(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            FacultyRepository facultyRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            ExamRepository examRepository,
            AttendanceRepository attendanceRepository,
            MarksRepository marksRepository,
            AssignmentRepository assignmentRepository,
            AssignmentSubmissionRepository submissionRepository,
            AcademicResultRepository resultRepository,
            PerformanceAlertRepository alertRepository,
            PerformancePredictionRepository predictionRepository,
            ResultService resultService,
            AlertService alertService,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed.department.name}") String departmentName,
            @Value("${app.seed.department.code}") String departmentCode,
            @Value("${app.seed.admin.username}") String adminUsername,
            @Value("${app.seed.admin.email}") String adminEmail,
            @Value("${app.seed.admin.password}") String adminPassword,
            @Value("${app.seed.faculty.username}") String facultyUsername,
            @Value("${app.seed.faculty.email}") String facultyEmail,
            @Value("${app.seed.faculty.password}") String facultyPassword,
            @Value("${app.seed.student.username}") String studentUsername,
            @Value("${app.seed.student.email}") String studentEmail,
            @Value("${app.seed.student.password}") String studentPassword) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.examRepository = examRepository;
        this.attendanceRepository = attendanceRepository;
        this.marksRepository = marksRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.resultRepository = resultRepository;
        this.alertRepository = alertRepository;
        this.predictionRepository = predictionRepository;
        this.resultService = resultService;
        this.alertService = alertService;
        this.passwordEncoder = passwordEncoder;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.adminUsername = adminUsername;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.facultyUsername = facultyUsername;
        this.facultyEmail = facultyEmail;
        this.facultyPassword = facultyPassword;
        this.studentUsername = studentUsername;
        this.studentEmail = studentEmail;
        this.studentPassword = studentPassword;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Department department = ensureDepartment();

        User admin = ensureUser(adminUsername, adminEmail, adminPassword, Role.ADMIN);
        User facultyUser = ensureUser(facultyUsername, facultyEmail, facultyPassword, Role.FACULTY);
        User studentUser = ensureUser(studentUsername, studentEmail, studentPassword, Role.STUDENT);

        Faculty faculty = ensureFacultyProfile(facultyUser, department);
        Student student = ensureStudentProfile(studentUser, department);

        int studentCount = 0;
        if (faculty != null) {
            AcademicContext context = ensureAcademicDemoData(department, faculty);

            List<Student> students = new ArrayList<>();
            Map<Long, Tier> tiers = new HashMap<>();
            if (student != null) {
                students.add(student);
                tiers.put(student.getId(), Tier.AVERAGE);
            }
            for (Roster roster : ROSTER) {
                Student seeded = ensureRosterStudent(department, roster);
                if (seeded != null) {
                    students.add(seeded);
                    tiers.put(seeded.getId(), roster.tier());
                }
            }
            for (int i = 0; i < students.size(); i++) {
                Student current = students.get(i);
                seedAcademicHistory(context, current, i, tiers.get(current.getId()));
            }
            studentCount = students.size();
            generateAlerts(students);
        }

        log.info("Demo seed check complete. Accounts: admin='{}', faculty='{}', student='{}'. "
                        + "Roster size: {}. Passwords are stored as BCrypt hashes only - "
                        + "see the README for local dev credentials.",
                admin.getUsername(), facultyUser.getUsername(), studentUser.getUsername(), studentCount);
    }

    // ------------------------------------------------------------- accounts

    private Department ensureDepartment() {
        return departmentRepository.findByCode(departmentCode)
                .orElseGet(() -> {
                    log.info("Seeding department '{}' ({})", departmentName, departmentCode);
                    return departmentRepository.save(new Department(departmentName, departmentCode));
                });
    }

    private User ensureUser(String username, String email, String rawPassword, Role role) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            User user = new User();
                            user.setUsername(username);
                            user.setEmail(email);
                            user.setPasswordHash(passwordEncoder.encode(rawPassword));
                            user.setRole(role);
                            user.setEnabled(true);
                            log.info("Seeding demo {} account '{}'", role, username);
                            return userRepository.save(user);
                        }));
    }

    private Faculty ensureFacultyProfile(User facultyUser, Department department) {
        return facultyRepository.findByUser_Username(facultyUser.getUsername())
                .orElseGet(() -> {
                    if (facultyRepository.existsByEmployeeCode("FAC1001")) {
                        log.warn("Skipping demo faculty profile: employee code FAC1001 already used.");
                        return null;
                    }
                    Faculty profile = new Faculty();
                    profile.setUser(facultyUser);
                    profile.setEmployeeCode("FAC1001");
                    profile.setFirstName("Demo");
                    profile.setLastName("Faculty");
                    profile.setEmail(facultyEmail);
                    profile.setDesignation("Assistant Professor");
                    profile.setDepartment(department);
                    log.info("Seeding demo faculty profile for '{}'", facultyUser.getUsername());
                    return facultyRepository.save(profile);
                });
    }

    private Student ensureStudentProfile(User studentUser, Department department) {
        return studentRepository.findByUser_Username(studentUser.getUsername())
                .orElseGet(() -> {
                    if (studentRepository.existsByRollNumber("CS2023001")) {
                        log.warn("Skipping demo student profile: roll number CS2023001 already used.");
                        return null;
                    }
                    Student profile = new Student();
                    profile.setUser(studentUser);
                    profile.setRollNumber("CS2023001");
                    profile.setRegistrationNumber("REG2023001");
                    profile.setFirstName("Demo");
                    profile.setLastName("Student");
                    profile.setEmail(studentEmail);
                    profile.setCurrentSemester(3);
                    profile.setAdmissionYear(2023);
                    profile.setDepartment(department);
                    log.info("Seeding demo student profile for '{}'", studentUser.getUsername());
                    return studentRepository.save(profile);
                });
    }

    private Student ensureRosterStudent(Department department, Roster roster) {
        return studentRepository.findByRollNumber(roster.rollNumber()).orElseGet(() -> {
            String registrationNumber = "REG" + roster.rollNumber().substring(2);
            String email = roster.rollNumber().toLowerCase() + "@campus.edu";
            if (studentRepository.existsByRegistrationNumber(registrationNumber)
                    || studentRepository.existsByEmail(email)) {
                log.warn("Skipping demo student '{}': registration number or email already used.",
                        roster.rollNumber());
                return null;
            }
            Student profile = new Student();
            profile.setRollNumber(roster.rollNumber());
            profile.setRegistrationNumber(registrationNumber);
            profile.setFirstName(roster.firstName());
            profile.setLastName(roster.lastName());
            profile.setEmail(email);
            profile.setCurrentSemester(3);
            profile.setAdmissionYear(2023);
            profile.setDepartment(department);
            log.info("Seeding demo student profile '{}'", roster.rollNumber());
            return studentRepository.save(profile);
        });
    }

    // -------------------------------------------------------- academic data

    /**
     * The subjects, exams and assignments every student is assessed against.
     * These are shared, so they are created once and reused across the roster.
     */
    private record AcademicContext(List<Subject> currentSubjects,
                                   List<Exam> currentExams,
                                   List<Subject> previousSubjects,
                                   List<Assignment> assignments) {
    }

    /**
     * Seeds three "current" subjects (with a mid-term exam each), three
     * "previous" subjects to grade, and six assignments with a mix of past and
     * future due dates.
     */
    private AcademicContext ensureAcademicDemoData(Department department, Faculty faculty) {
        Subject dataStructures = ensureSubject("CS301", "Data Structures", 3, 4, department, faculty);
        Subject dbms = ensureSubject("CS302", "Database Management Systems", 3, 3, department, faculty);
        Subject operatingSystems = ensureSubject("CS303", "Operating Systems", 3, 3, department, faculty);

        Subject discreteMaths = ensureSubject("CS201", "Discrete Mathematics", 2, 4, department, faculty);
        Subject computerOrganisation = ensureSubject("CS202", "Computer Organisation", 2, 3, department, faculty);
        Subject oop = ensureSubject("CS203", "Object Oriented Programming", 2, 3, department, faculty);

        Exam dsExam = ensureExam(dataStructures, "Mid-Term", 100, LocalDate.now().minusDays(35));
        Exam dbmsExam = ensureExam(dbms, "Mid-Term", 100, LocalDate.now().minusDays(33));
        Exam osExam = ensureExam(operatingSystems, "Mid-Term", 100, LocalDate.now().minusDays(31));

        LocalDate today = LocalDate.now();
        List<Assignment> assignments = List.of(
                ensureAssignment(dataStructures, faculty, "Arrays and linked lists", 100, today.minusDays(24)),
                ensureAssignment(dbms, faculty, "Normalisation exercise", 100, today.minusDays(21)),
                ensureAssignment(operatingSystems, faculty, "CPU scheduling report", 100, today.minusDays(18)),
                ensureAssignment(dataStructures, faculty, "Graph traversal lab", 100, today.minusDays(6)),
                ensureAssignment(dbms, faculty, "SQL query practice", 100, today.plusDays(3)),
                ensureAssignment(operatingSystems, faculty, "Memory management essay", 100, today.plusDays(7)));

        return new AcademicContext(
                List.of(dataStructures, dbms, operatingSystems),
                List.of(dsExam, dbmsExam, osExam),
                List.of(discreteMaths, computerOrganisation, oop),
                assignments);
    }

    /** Seeds one student's attendance, marks, results and submissions (once). */
    private void seedAcademicHistory(AcademicContext context, Student student, int index, Tier tier) {
        if (attendanceRepository.countByStudentId(student.getId()) == 0) {
            for (int si = 0; si < context.currentSubjects().size(); si++) {
                int pct = tier.attendancePct + (Math.floorMod(index + si, 5) - 2);
                pct = Math.max(0, Math.min(100, pct));
                int present = (int) Math.round(pct / 100.0 * SESSION_COUNT);
                seedAttendance(student, context.currentSubjects().get(si), present, SESSION_COUNT);
            }
        }

        if (marksRepository.countByStudentId(student.getId()) == 0) {
            for (int ei = 0; ei < context.currentExams().size(); ei++) {
                seedMarks(student, context.currentExams().get(ei), score(tier.marksBase, index, ei, 0));
            }
        }

        if (resultRepository.countByStudentId(student.getId()) == 0) {
            double[] offsets = {0.4, 0.0, -0.4};
            List<Subject> previous = context.previousSubjects();
            for (int pi = 0; pi < previous.size(); pi++) {
                Subject subject = previous.get(pi);
                double points = tier.gradeBase + offsets[pi]
                        + ((Math.floorMod(index, 3) - 1) * 0.2);
                points = Math.max(4.0, Math.min(10.0, points));
                BigDecimal gradePoints = BigDecimal.valueOf(points).setScale(2, RoundingMode.HALF_UP);
                resultService.create(new ResultRequest(
                        student.getId(), subject.getId(), 2, gradeFor(points),
                        gradePoints, subject.getCredits()));
            }
            log.info("Seeding demo results and CGPA for student '{}'", student.getRollNumber());
        }

        if (submissionRepository.findByStudentId(student.getId()).isEmpty()) {
            seedSubmissions(student, index, tier, context.assignments());
        }
    }

    // ------------------------------------------------------------- helpers

    private Subject ensureSubject(String code, String name, int semester, int credits,
                                  Department department, Faculty faculty) {
        return subjectRepository.findByCode(code)
                .orElseGet(() -> {
                    Subject subject = new Subject();
                    subject.setCode(code);
                    subject.setName(name);
                    subject.setSemester(semester);
                    subject.setCredits(credits);
                    subject.setDepartment(department);
                    subject.setFaculty(faculty);
                    log.info("Seeding subject '{}' ({})", code, name);
                    return subjectRepository.save(subject);
                });
    }

    private Exam ensureExam(Subject subject, String name, int maxMarks, LocalDate examDate) {
        for (Exam exam : examRepository.findBySubjectId(subject.getId())) {
            if (exam.getName().equalsIgnoreCase(name)) {
                return exam;
            }
        }
        Exam exam = new Exam();
        exam.setName(name);
        exam.setSubject(subject);
        exam.setMaxMarks(maxMarks);
        exam.setExamDate(examDate);
        return examRepository.save(exam);
    }

    private Assignment ensureAssignment(Subject subject, Faculty faculty, String title,
                                        int maxMarks, LocalDate dueDate) {
        for (Assignment assignment : assignmentRepository.findBySubjectId(subject.getId())) {
            if (assignment.getTitle().equalsIgnoreCase(title)) {
                return assignment;
            }
        }
        Assignment assignment = new Assignment();
        assignment.setTitle(title);
        assignment.setDescription("Seeded demo coursework for " + subject.getCode() + ".");
        assignment.setSubject(subject);
        assignment.setFaculty(faculty);
        assignment.setMaxMarks(maxMarks);
        assignment.setDueDate(dueDate);
        return assignmentRepository.save(assignment);
    }

    /** Records {@code total} sessions ending a few days ago; the first
     *  {@code present} are PRESENT and the remainder are ABSENT. */
    private void seedAttendance(Student student, Subject subject, int present, int total) {
        LocalDate start = LocalDate.now().minusDays(total + 4L);
        for (int i = 0; i < total; i++) {
            Attendance attendance = new Attendance();
            attendance.setStudent(student);
            attendance.setSubject(subject);
            attendance.setClassDate(start.plusDays(i));
            attendance.setStatus(i < present ? AttendanceStatus.PRESENT : AttendanceStatus.ABSENT);
            attendanceRepository.save(attendance);
        }
    }

    private void seedMarks(Student student, Exam exam, BigDecimal marksObtained) {
        Marks marks = new Marks();
        marks.setStudent(student);
        marks.setExam(exam);
        marks.setMarksObtained(marksObtained);
        marksRepository.save(marks);
    }

    /**
     * Gives one student a deterministic mix of submission states: strong and
     * average students mostly submit (some late), weak students leave some work
     * pending, so the dashboard and analytics show pending/late/graded rows.
     */
    private void seedSubmissions(Student student, int index, Tier tier, List<Assignment> assignments) {
        LocalDate today = LocalDate.now();
        for (int a = 0; a < assignments.size(); a++) {
            Assignment assignment = assignments.get(a);
            boolean past = !assignment.getDueDate().isAfter(today);
            int variant = Math.floorMod(index + a, 4);

            AssignmentSubmission submission = new AssignmentSubmission();
            submission.setAssignment(assignment);
            submission.setStudent(student);

            if (tier == Tier.STRONG) {
                if (past) {
                    submission.setStatus(SubmissionStatus.GRADED);
                    submission.setMarks(score(tier.marksBase, index, a, 6));
                    submission.setFeedback("Excellent work.");
                } else {
                    submission.setStatus(SubmissionStatus.SUBMITTED);
                }
            } else if (tier == Tier.AVERAGE) {
                if (!past) {
                    submission.setStatus(SubmissionStatus.SUBMITTED);
                } else if (variant == 3) {
                    submission.setStatus(SubmissionStatus.LATE);
                    submission.setMarks(score(tier.marksBase, index, a, -6));
                    submission.setFeedback("Submitted late - revise the topic.");
                } else {
                    submission.setStatus(SubmissionStatus.GRADED);
                    submission.setMarks(score(tier.marksBase, index, a, 2));
                    submission.setFeedback("Satisfactory.");
                }
            } else {
                if (variant == 0) {
                    submission.setStatus(SubmissionStatus.PENDING);
                } else if (!past) {
                    submission.setStatus(variant == 2 ? SubmissionStatus.SUBMITTED : SubmissionStatus.PENDING);
                } else if (variant == 1) {
                    submission.setStatus(SubmissionStatus.LATE);
                    submission.setMarks(score(tier.marksBase, index, a, -4));
                    submission.setFeedback("Late submission.");
                } else {
                    submission.setStatus(SubmissionStatus.GRADED);
                    submission.setMarks(score(tier.marksBase, index, a, 0));
                    submission.setFeedback("Needs significant improvement.");
                }
            }
            submissionRepository.save(submission);
        }
    }

    /**
     * Runs the early-warning rules for each demo student that has not been
     * evaluated yet. A student is considered already evaluated once a prediction
     * exists (the alert run always records one), which makes restarts fully
     * idempotent and never piles up duplicate predictions or alerts.
     */
    private void generateAlerts(List<Student> students) {
        int raised = 0;
        int evaluated = 0;
        for (Student student : students) {
            boolean alreadyEvaluated = !alertRepository
                    .findByStudentIdOrderByCreatedAtDesc(student.getId()).isEmpty()
                    || predictionRepository
                    .findTopByStudentIdOrderByGeneratedAtDesc(student.getId()).isPresent();
            if (alreadyEvaluated) {
                continue;
            }
            raised += alertService.generateForStudent(student.getId()).size();
            evaluated++;
        }
        if (evaluated > 0) {
            log.info("Generated {} early-warning alert(s) for {} newly evaluated demo student(s).",
                    raised, evaluated);
        }
    }

    // -------------------------------------------------------------- helpers

    /** Deterministic score around a tier base, clamped to 0-100. */
    private BigDecimal score(int base, int index, int slot, int delta) {
        int value = base + (Math.floorMod(index * 2 + slot, 9) - 4) + delta;
        value = Math.max(0, Math.min(100, value));
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private String gradeFor(double points) {
        if (points >= 9.0) {
            return "A+";
        }
        if (points >= 8.0) {
            return "A";
        }
        if (points >= 7.0) {
            return "B+";
        }
        if (points >= 6.0) {
            return "B";
        }
        if (points >= 5.0) {
            return "C";
        }
        if (points >= 4.0) {
            return "D";
        }
        return "F";
    }
}
