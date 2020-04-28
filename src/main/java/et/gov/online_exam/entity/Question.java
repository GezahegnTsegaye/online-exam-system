package et.gov.online_exam.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public @Data class Question {

	@Id
	@GeneratedValue
	private Long questionId;

	@Column
	private Integer numberOfPages;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "examId")
	private Exam exam;
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
	private String answer;
	private String analyse;
	private Double score;
	private Double finalScore;

	private String optionAChecked;
	private String optionBChecked;
	private String optionCChecked;
	private String optionDChecked;
	private String judgeAnswer1;
	private String judgeAnswer0;

	private String textAnswerStu;
	private String optionACheckedStu;
	private String optionBCheckedStu;
	private String optionCCheckedStu;
	private String optionDCheckedStu;
	private String judgeAnswer1Stu;
	private String judgeAnswer0Stu;

}
