/**
 * JPA entities (database tables) live here.
 *
 * <p>Each class in this package maps to exactly one MySQL table using
 * {@code @Entity}. Entities are the ONLY place that knows about database
 * columns; they are never returned directly from a REST controller - the
 * {@code dto} package handles that.
 *
 * <p>The 13 entities in this package are: User, Student, Faculty, Department,
 * Subject, Attendance, Exam, Marks, Assignment, AssignmentSubmission,
 * AcademicResult, PerformancePrediction and PerformanceAlert. All of them share
 * the id + timestamp fields from {@code BaseEntity}.
 */
package com.academic.performance.entity;
