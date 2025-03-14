package com.exam.dal.model;

import com.exam.dal.model.Course;
import com.exam.dal.model.ExamStatus;
import com.exam.dal.model.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "published")
    private boolean published;

    @Column(name = "total_marks")
    private Double totalMarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExamStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany(mappedBy = "exam",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Question> questions = new HashSet<>();

    /**
     * Adds a question to the exam
     * @param question The question to add
     */
    public void addQuestion(Question question) {
        questions.add(question);
        question.setExam(this);
    }

    /**
     * Removes a question from the exam
     * @param question The question to remove
     */
    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setExam(null);
    }
}