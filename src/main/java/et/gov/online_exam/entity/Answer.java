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
@NoArgsConstructor
@AllArgsConstructor
public @Data class Answer {
	
	
	@Id
	@GeneratedValue
	private Long answerId;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private Question question;
    @Column
    private String name; // answer
    @Column(name = "IS_CORRECT", nullable = false)
    private boolean correct = false; // is correct answer
	

}
