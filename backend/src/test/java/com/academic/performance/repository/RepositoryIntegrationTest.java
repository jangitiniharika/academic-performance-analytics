package com.academic.performance.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.entity.Attendance;
import com.academic.performance.entity.AttendanceStatus;
import com.academic.performance.entity.Department;
import com.academic.performance.entity.Exam;
import com.academic.performance.entity.Marks;
import com.academic.performance.entity.Student;
import com.academic.performance.entity.Subject;

/**
 * Integration test that ACTUALLY writes to and reads from MySQL through the
 * repositories, proving the entity mappings and relationships work.
 *
 * <p>{@code @Transactional} means every test runs in a transaction that Spring
 * rolls back afterwards, so the development database is never polluted.
 */
@SpringBootTest
@Transactional
class RepositoryIntegrationTest {

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ExamRepository examRepository;
    @Autowired
    private MarksRepository marksRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;

    private Department newDepartment() {
        // Use a unique code/name so the test never collides with demo data that
        // may already exist in the shared development database (for example the
        // seeder's "Computer Science (CS)" department).
        String suffix = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        Department department = new Department("Computer Science " + suffix, "CS" + suffix);
        return departmentRepository.save(department);
    }

    private Student newStudent(Department department, String roll) {
        Student student = new Student();
        student.setRollNumber(roll);
        student.setRegistrationNumber("REG-" + roll);
        student.setFirstName("Asha");
        student.setLastName("Kumar");
        student.setEmail(roll.toLowerCase() + "@campus.edu");
        student.setCurrentSemester(3);
        student.setDepartment(department);
        return studentRepository.save(student);
    }

    @Test
    void persistsStudentsSubjectsExamsAndComputesMarksPercentage() {
        Department department = newDepartment();

        Subject subject = new Subject();
        // Unique code so the test never collides with demo/seed data that may
        // already exist in the shared development database (for example CS301).
        subject.setCode("TS" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8));
        subject.setName("Database Management Systems");
        subject.setSemester(3);
        subject.setCredits(4);
        subject.setDepartment(department);
        subject = subjectRepository.save(subject);

        String roll = "T" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 9);
        Student student = newStudent(department, roll);

        Exam exam = new Exam();
        exam.setName("Mid-Term 1");
        exam.setSubject(subject);
        exam.setExamDate(LocalDate.of(2026, 1, 15));
        exam.setMaxMarks(40);
        exam = examRepository.save(exam);

        Marks marks = new Marks();
        marks.setStudent(student);
        marks.setExam(exam);
        marks.setMarksObtained(new BigDecimal("30.00"));
        marks = marksRepository.saveAndFlush(marks);

        // The @PrePersist callback must have derived the percentage (30/40 = 75%).
        assertThat(marks.getPercentage()).isEqualByComparingTo("75.00");

        // Derived repository queries work as expected.
        assertThat(studentRepository.findByRollNumber(roll)).isPresent();
        assertThat(marksRepository.findByStudentIdAndExamId(student.getId(), exam.getId())).isPresent();
        assertThat(marksRepository.findByStudentIdAndExam_SubjectId(student.getId(), subject.getId()))
                .hasSize(1);

        // Attendance + percentage aggregation basis.
        Attendance attendance = new Attendance();
        attendance.setStudent(student);
        attendance.setSubject(subject);
        attendance.setClassDate(LocalDate.now().minusDays(1));
        attendance.setStatus(AttendanceStatus.PRESENT);
        attendanceRepository.saveAndFlush(attendance);

        assertThat(attendanceRepository.countByStudentId(student.getId())).isEqualTo(1);
        assertThat(attendanceRepository.countByStudentIdAndStatus(student.getId(), AttendanceStatus.PRESENT))
                .isEqualTo(1);
    }

    @Test
    void preventsDuplicateRollNumbers() {
        Department department = newDepartment();
        String roll = "D" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 9);
        newStudent(department, roll);

        Student duplicate = new Student();
        duplicate.setRollNumber(roll);                 // same roll number
        duplicate.setRegistrationNumber("REG-OTHER");
        duplicate.setFirstName("Copy");
        duplicate.setLastName("Cat");
        duplicate.setEmail("duplicate@campus.edu");
        duplicate.setCurrentSemester(3);
        duplicate.setDepartment(department);

        assertThatThrownBy(() -> studentRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
