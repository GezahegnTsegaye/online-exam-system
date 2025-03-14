package com.exam.dal.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a faculty or college within the institution
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "faculties")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "description", length = 1000)
    private String description;

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Department> departments = new HashSet<>();

    /**
     * Adds a department to the faculty
     * @param department The department to add
     * @return The updated department
     */
    public Department addDepartment(Department department) {
        departments.add(department);
        department.setFaculty(this);
        return department;
    }

    /**
     * Removes a department from the faculty
     * @param department The department to remove
     */
    public void removeDepartment(Department department) {
        departments.remove(department);
        department.setFaculty(null);
    }
}