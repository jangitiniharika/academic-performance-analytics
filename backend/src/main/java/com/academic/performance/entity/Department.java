package com.academic.performance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * An academic department (for example Computer Science).
 *
 * <p>Students, Faculty and Subjects all point back to a Department through
 * their own {@code @ManyToOne} field, so this entity needs no collection -
 * keeping the mapping unidirectional avoids circular references.
 */
@Entity
@Table(name = "departments")
public class Department extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @NotBlank
    @Size(max = 10)
    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    public Department() {
    }

    public Department(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
