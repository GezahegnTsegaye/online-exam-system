package et.gov.online_exam.dal.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "teacher", schema = "public")
public class Teacher implements Serializable {
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long courseId;

	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String emailAddress;
	private String address;

	@OneToMany(fetch = FetchType.LAZY, mappedBy= "teacher")
	private List<Course> courses = new ArrayList<>();

	@OneToMany(mappedBy="teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ExamReviewer> examReviewerList = new ArrayList<>();

}
