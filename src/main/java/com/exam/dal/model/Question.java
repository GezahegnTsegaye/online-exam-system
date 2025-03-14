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
@Table(name = "questions")
public class Question {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "content", nullable = false, length = 1000)
	private String content;

	@Column(name = "marks")
	private Integer marks;

	@Enumerated(EnumType.STRING)
	@Column(name = "question_type", nullable = false)
	private QuestionType questionType;

	@Column(name = "difficulty_level")
	private String difficultyLevel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exam_id")
	private Exam exam;

	@OneToMany(mappedBy = "question",
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.LAZY)
	private Set<Option> options = new HashSet<>();

	/**
	 * Adds an option to the question
	 * @param option The option to add
	 */
	public void addOption(Option option) {
		options.add(option);
		option.setQuestion(this);
	}

	/**
	 * Removes an option from the question
	 * @param option The option to remove
	 */
	public void removeOption(Option option) {
		options.remove(option);
		option.setQuestion(null);
	}
}