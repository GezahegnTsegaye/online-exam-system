package com.exam.dal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", nullable = false)
	private String title;

	@Column(name = "description", length = 1000)
	private String description;

	@Column(name = "code", unique = true)
	private String code;

	@Column(name = "credits")
	private Integer credits;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher_id")
	private User teacher;

	@ManyToMany(mappedBy = "courses")
	@Builder.Default
	private Set<Department> departments = new HashSet<>();

	@ManyToMany
	@JoinTable(
			name = "course_students",
			joinColumns = @JoinColumn(name = "course_id"),
			inverseJoinColumns = @JoinColumn(name = "student_id")
	)
	@Builder.Default
	private Set<User> students = new HashSet<>();

	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private Set<Exam> exams = new HashSet<>();

	/**
	 * Adds a student to the course
	 * @param student The student to add
	 */
	public void addStudent(User student) {
		students.add(student);
	}

	/**
	 * Removes a student from the course
	 * @param student The student to remove
	 */
	public void removeStudent(User student) {
		students.remove(student);
	}
}