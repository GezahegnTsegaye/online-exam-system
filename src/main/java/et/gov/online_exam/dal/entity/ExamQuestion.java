package et.gov.online_exam.dal.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "exam_question", schema="public")
public class ExamQuestion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examQuestionId;

    @Column
    private String examName;

    @Column
    private LocalDate examDate;

    @Column
    private Long examStartTime;

    @Column
    private Long examEndTime;

    @Column
    private Double score;

    @Column
    private Double finalScore;

    @Column(name = "CORRECT_ANSWERS")
    private Integer correctAnswers;

    @Column
    private Integer grade;

    private String examCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "examReviewerId")
    private ExamReviewer examReviewer;


    @OneToMany(mappedBy ="examQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy ="examQuestion", cascade = CascadeType.ALL)
    List<Question> questions = new ArrayList<>();
}
