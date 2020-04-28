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
	
	
	
	
	
	
	
}
