package com.exam.dal.entity;

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
@Table(name = "questions")
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String content;

	private Integer marks;

	@Enumerated(EnumType.STRING)
	private QuestionType questionType;

	@ManyToOne
	@JoinColumn(name = "exam_id")
	private Exam exam;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Option> options = new HashSet<>();
}