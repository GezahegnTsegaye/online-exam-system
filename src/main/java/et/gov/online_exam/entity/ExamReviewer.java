package et.gov.online_exam.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class ExamReviewer {
	
	@Id
	@GeneratedValue
	private Long reviewerId;
	
	
	

}
