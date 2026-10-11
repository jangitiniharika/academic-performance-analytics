package com.academic.performance.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
import com.academic.performance.repository.StudentRepository;
import com.academic.performance.repository.SubjectRepository;
import com.academic.performance.repository.UserRepository;
import com.academic.performance.service.ResultService;

/**
 * Creates a small, realistic demo data set: three login accounts (Admin,
 * Faculty, Student), and enough academic history for one student that every
 * feature (attendance, marks, assignments, results/CGPA, analytics, the
 * transparent prediction and the early-warning alerts) has real data to show.
 *
 * <p>Every step is <strong>idempotent</strong>: rows are looked up by their
 * natural key first and only created when missing, so the seeder can run on
 * every start-up without duplicating or overwriting existing records. Passwords
 * are BCrypt-hashed before storage; the plain text only lives in configuration
 * and the README.
 *
 * <p>The whole seeder is disabled in tests ({@code app.seed.enabled=false}) and
 * can be switched off locally with {@code APP_SEED_ENABLED=false}.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

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
    private final ResultService resultService;
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
            ResultService resultService,
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
        this.resultService = resultService;
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

        if (faculty != null && student != null) {
            ensureAcademicDemoData(department, faculty, student);
        }

        log.info("Demo seed check complete. Accounts: admin='{}', faculty='{}', student='{}'. "
                        + "Passwords are stored as BCrypt hashes only - see the README for local dev credentials.",
                admin.getUsername(), facultyUser.getUsername(), studentUser.getUsername());
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
                .orElseGet(() -> {
                    // Guard against a different account already owning the same email.
                    if (userRepository.existsByEmail(email.toLowerCase())) {
                        User existing = userRepository.findByEmail(email.toLowerCase()).orElseThrow();
                        log.warn("Skipping demo {} user '{}': email {} already belongs to '{}'.",
                                role, username, email, existing.getUsername());
                        return existing;
                    }
                    log.info("Seeding {} account '{}'", role, username);
                    User user = new User(username, email.toLowerCase(),
                            passwordEncoder.encode(rawPassword), role);
                    user.setEnabled(true);
                    return userRepository.save(user);
                });
    }

    private Faculty ensureFacultyProfile(User facultyUser, Department department) {
        return facultyRepository.findByUser_Username(facultyUser.getUsername())
                .orElseGet(() -> {
                    String employeeCode = "FAC1001";
                    if (facultyRepository.existsByEmployeeCode(employeeCode)
                            || facultyRepository.existsByEmail(facultyUser.getEmail())) {
                        log.warn("Skipping demo faculty profile for '{}': employee code or email already used.",
                                facultyUser.getUsername());
                        return null;
                    }
                    Faculty faculty = new Faculty();
                    faculty.setEmployeeCode(employeeCode);
                    faculty.setFirstName("Demo");
                    faculty.setLastName("Faculty");
                    faculty.setEmail(facultyUser.getEmail());
                    faculty.setDesignation("Assistant Professor");
                    faculty.setDepartment(department);
                    faculty.setUser(facultyUser);
                    log.info("Seeding faculty profile '{}'", employeeCode);
                    return facultyRepository.save(faculty);
                });
    }

    private Student ensureStudentProfile(User studentUser, Department department) {
        return studentRepository.findByUser_Username(studentUser.getUsername())
                .orElseGet(() -> {
                    String rollNumber = "CS2023001";
                    String registrationNumber = "REG2023001";
                    if (studentRepository.existsByRollNumber(rollNumber)
                            || studentRepository.existsByRegistrationNumber(registrationNumber)
                            || studentRepository.existsByEmail(studentUser.getEmail())) {
                        log.warn("Skipping demo student profile for '{}': roll/registration/email already used.",
                                studentUser.getUsername());
                        return null;
                    }
                    Student student = new Student();
                    student.setRollNumber(rollNumber);
                    student.setRegistrationNumber(registrationNumber);
                    student.setFirstName("Demo");
                    student.setLastName("Student");
                    student.setEmail(studentUser.getEmail());
                    student.setCurrentSemester(3);
                    student.setAdmissionYear(2023);
                    student.setDepartment(department);
                    student.setUser(studentUser);
                    log.info("Seeding student profile '{}'", rollNumber);
                    return studentRepository.save(student);
                });
    }

    // -------------------------------------------------------- academic data

    /**
     * Seeds three "current" subjects (with a mid-term exam each) plus three
     * "previous" subjects with grades. Attendance is deliberately mixed (one
     * subject is below the 75% threshold) and one mark is below 50, so the
     * early-warning rules have something meaningful to report.
     */
    private void ensureAcademicDemoData(Department department, Faculty faculty, Student student) {
        Subject dataStructures = ensureSubject("CS301", "Data Structures", 3, 4, department, faculty);
        Subject dbms = ensureSubject("CS302", "Database Management Systems", 3, 3, department, faculty);
        Subject operatingSystems = ensureSubject("CS303", "Operating Systems", 3, 3, department, faculty);

        Subject discreteMaths = ensureSubject("CS201", "Discrete Mathematics", 2, 4, department, faculty);
        Subject computerOrganisation = ensureSubject("CS202", "Computer Organisation", 2, 3, department, faculty);
        Subject oop = ensureSubject("CS203", "Object Oriented Programming", 2, 3, department, faculty);

        Exam dsExam = ensureExam(dataStructures, "Mid-Term", 100, LocalDate.now().minusDays(35));
        Exam dbmsExam = ensureExam(dbms, "Mid-Term", 100, LocalDate.now().minusDays(33));
        Exam osExam = ensureExam(operatingSystems, "Mid-Term", 100, LocalDate.now().minusDays(31));

        if (attendanceRepository.countByStudentId(student.getId()) == 0) {
            seedAttendance(student, dataStructures, 12, 15);
            seedAttendance(student, dbms, 12, 15);
            seedAttendance(student, operatingSystems, 9, 15);
            log.info("Seeding demo attendance for student '{}'", student.getRollNumber());
        }

        if (marksRepository.countByStudentId(student.getId()) == 0) {
            seedMarks(student, dsExam, new BigDecimal("78.00"));
            seedMarks(student, dbmsExam, new BigDecimal("72.00"));
            seedMarks(student, osExam, new BigDecimal("45.00"));
            log.info("Seeding demo marks for student '{}'", student.getRollNumber());
        }

        if (submissionRepository.findByStudentId(student.getId()).isEmpty()) {
            Assignment dsAssignment = ensureAssignment(dataStructures, faculty,
                    "Arrays and linked lists", 100, LocalDate.now().minusDays(20));
            Assignment dbmsAssignment = ensureAssignment(dbms, faculty,
                    "Normalisation exercise", 100, LocalDate.now().minusDays(18));
            Assignment osAssignment = ensureAssignment(operatingSystems, faculty,
                    "CPU scheduling report", 100, LocalDate.now().minusDays(16));
            seedSubmission(student, dsAssignment, new BigDecimal("85.00"), "Clear and well structured.");
            seedSubmission(student, dbmsAssignment, new BigDecimal("80.00"), "Good normalisation to 3NF.");
            seedSubmission(student, osAssignment, new BigDecimal("55.00"), "Revise scheduling algorithms.");
            log.info("Seeding demo assignments and submissions for student '{}'", student.getRollNumber());
        }

        if (resultRepository.countByStudentId(student.getId()) == 0) {
            // Previous semester results -> the "previous performance" indicator
            // and CGPA (estimated 8.70 / 10).
            resultService.create(new ResultRequest(student.getId(), discreteMaths.getId(), 2, "A",
                    new BigDecimal("9.00"), 4));
            resultService.create(new ResultRequest(student.getId(), computerOrganisation.getId(), 2, "B+",
                    new BigDecimal("8.00"), 3));
            resultService.create(new ResultRequest(student.getId(), oop.getId(), 2, "A",
                    new BigDecimal("9.00"), 3));
            log.info("Seeding demo results and CGPA for student '{}'", student.getRollNumber());
        }
    }

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
        List<Exam> existing = examRepository.findBySubjectId(subject.getId());
        for (Exam exam : existing) {
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

    private void seedSubmission(Student student, Assignment assignment,
                                BigDecimal marks, String feedback) {
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setStatus(SubmissionStatus.GRADED);
        submission.setMarks(marks);
        submission.setFeedback(feedback);
        submissionRepository.save(submission);
    }
}
