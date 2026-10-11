package com.academic.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.PageResponse;
import com.academic.performance.dto.StudentRequest;
import com.academic.performance.dto.StudentResponse;
import com.academic.performance.entity.Department;
import com.academic.performance.entity.Student;
import com.academic.performance.entity.User;
import com.academic.performance.exception.DuplicateResourceException;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.mapper.StudentMapper;
import com.academic.performance.repository.DepartmentRepository;
import com.academic.performance.repository.StudentRepository;
import com.academic.performance.repository.UserRepository;
import com.academic.performance.util.TextUtils;

/** Business logic for Students (Stage 4). */
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapper;

    public StudentService(StudentRepository studentRepository,
                          DepartmentRepository departmentRepository,
                          UserRepository userRepository,
                          StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.studentMapper = studentMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> list(Long departmentId, String search, Pageable pageable) {
        Page<Student> page = studentRepository.search(departmentId, TextUtils.normalizeSearch(search), pageable);
        return PageResponse.from(page.map(studentMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public StudentResponse get(Long id) {
        return studentMapper.toResponse(getEntity(id));
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        String rollNumber = request.getRollNumber().trim();
        String registrationNumber = request.getRegistrationNumber().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (studentRepository.existsByRollNumber(rollNumber)) {
            throw new DuplicateResourceException("Roll number", rollNumber);
        }
        if (studentRepository.existsByRegistrationNumber(registrationNumber)) {
            throw new DuplicateResourceException("Registration number", registrationNumber);
        }
        if (studentRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Student email", email);
        }

        Student student = new Student();
        applyRequest(student, request, rollNumber, registrationNumber, email);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = getEntity(id);
        String rollNumber = request.getRollNumber().trim();
        String registrationNumber = request.getRegistrationNumber().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (!student.getRollNumber().equalsIgnoreCase(rollNumber) && studentRepository.existsByRollNumber(rollNumber)) {
            throw new DuplicateResourceException("Roll number", rollNumber);
        }
        if (!student.getRegistrationNumber().equalsIgnoreCase(registrationNumber)
                && studentRepository.existsByRegistrationNumber(registrationNumber)) {
            throw new DuplicateResourceException("Registration number", registrationNumber);
        }
        if (!student.getEmail().equalsIgnoreCase(email) && studentRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Student email", email);
        }

        applyRequest(student, request, rollNumber, registrationNumber, email);
        return studentMapper.toResponse(studentRepository.save(student));
    }

    @Transactional
    public void delete(Long id) {
        studentRepository.delete(getEntity(id));
    }

    /** Used by {@code GET /api/student/me} - the profile behind the logged-in account. */
    @Transactional(readOnly = true)
    public StudentResponse findByUsername(String username) {
        Student student = studentRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile", "username", username));
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public Student getEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    /** Resolves the student behind a login account (for the STUDENT self endpoints). */
    @Transactional(readOnly = true)
    public Student requireEntityByUsername(String username) {
        return studentRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile", "username", username));
    }

    private void applyRequest(Student student, StudentRequest request,
                              String rollNumber, String registrationNumber, String email) {
        student.setRollNumber(rollNumber);
        student.setRegistrationNumber(registrationNumber);
        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setEmail(email);
        student.setPhone(TextUtils.trimToNull(request.getPhone()));
        student.setCurrentSemester(request.getCurrentSemester());
        student.setCgpa(request.getCgpa());
        student.setAdmissionYear(request.getAdmissionYear());
        student.setDepartment(resolveDepartment(request.getDepartmentId()));
        student.setUser(resolveUser(request.getUserId()));
    }

    private Department resolveDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));
    }

    private User resolveUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}
