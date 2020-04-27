package et.gov.online_exam.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public @Data class Exam {
	
	@Id
	@GeneratedValue
	private Long examId;
	
	@Column
	private String examName;
	
	@Column
	private Date examDate;
	
	@Column
	private Long examStartTime;
	
	@Column 
	private Long examEndTime;
	
	private ExamReviewer examReviewer;
	
	
	@Column
	private Double score;
	
	@Column
	private Double finalScore;
	
	
	private Student student;
	

}
