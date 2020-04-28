package et.gov.online_exam.entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class Score {

	@Id
	@GeneratedValue
	private Integer scoreId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exam")
	private Exam exam;

	@Column
	private Double totalScore;
	@Column
	private String reading;
	@Column
	private String status;
}
