package et.gov.online_exam.dal.entity;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "answer_result", schema = "public")
public class AnswerResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerResultId;

    private Integer answerValues;
    private String answerName;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answerId")
    private Answer answer;

}
