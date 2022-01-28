package et.gov.online_exam.dal.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "question", schema="public")
public class Question implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long questionId;

	@Column
	private Integer numberOfPages;

	@Column
	private String name;

	@Column(name = "MULTI_ANSWER", nullable = false)
	private boolean multiAnswer = false;

	private String type;
	private String title;
	private String optionA;
	private String optionB;
	private String optionC;
	private String optionD;
	private Double score;
	private Double finalScore;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "examQuestionId")
	private ExamQuestion examQuestion;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	private List<Answer> answerList = new ArrayList<>();
}
