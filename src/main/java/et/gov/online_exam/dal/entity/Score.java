package et.gov.online_exam.dal.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "score", schema = "public")
public class Score implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer scoreId;

	@Column
	private Double totalScore;
	@Column
	private String reading;
	@Column
	private String status;



}
