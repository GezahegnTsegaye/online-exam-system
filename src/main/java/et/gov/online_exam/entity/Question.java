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
public @Data class Question {

	
	@Id
	@GeneratedValue
	private Long questionId;
	
	private Exam exam;
	
	@Column
	private Integer numberOfPages;
	
	
	
	
	
	
	
	
}
