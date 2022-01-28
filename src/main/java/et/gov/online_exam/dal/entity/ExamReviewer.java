package et.gov.online_exam.dal.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "exam_reviewer", schema = "public")
public class ExamReviewer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long examReviewerId;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date start;

	@Column(name = "QUESTION_COUNT")
	private Integer questionCount;

	@Column(name = "CORRECT_ANSWERS")
	private Integer correctAnswers;

	@Column
	private Integer grade;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "teacherId", nullable = false)
	private Teacher teacher;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "questionId")
	private Question question;

	@OneToMany(mappedBy="examReviewer", cascade = CascadeType.ALL)
	private List<ExamQuestion> examsExamQuestions = new ArrayList<>();
}
