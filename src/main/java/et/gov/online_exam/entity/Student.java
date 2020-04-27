package et.gov.online_exam.entity;

import javax.annotation.Generated;
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
public @Data class Student {
	
	@Id
	@GeneratedValue
	private Long studentId;
	
	@Column
	private String firstName;
	
	@Column
	private String lastName;
	
	

}
