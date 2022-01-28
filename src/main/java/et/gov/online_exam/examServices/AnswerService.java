package et.gov.online_exam.examServices;

import et.gov.online_exam.dal.entity.Answer;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AnswerService {
    Answer insertAnswer(Answer answer);

    List<Answer> getAnswersList();

    Answer getAnswers(long answerId);
}
