package com.academic.performance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Academic Performance Analytics &amp; Prediction Platform backend.
 *
 * <p>{@code @SpringBootApplication} is a convenience annotation that combines:
 * <ul>
 *   <li>{@code @Configuration}   - this class can declare Spring beans,</li>
 *   <li>{@code @EnableAutoConfiguration} - Spring Boot configures Tomcat, JPA, Security, ...</li>
 *   <li>{@code @ComponentScan}   - everything under {@code com.academic.performance} is scanned.</li>
 * </ul>
 *
 * <p>Run with: {@code mvn spring-boot:run} (from the {@code backend} folder).
 */
@SpringBootApplication
public class AcademicPerformanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademicPerformanceApplication.class, args);
    }
}
