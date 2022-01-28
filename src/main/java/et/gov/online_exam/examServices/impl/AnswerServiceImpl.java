package et.gov.online_exam.examServices.impl;


import et.gov.online_exam.dal.entity.Answer;
import et.gov.online_exam.dal.repository.AnswerRepository;
import et.gov.online_exam.examServices.AnswerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository) {

        this.answerRepository = answerRepository;
    }

    @Override
    public Answer insertAnswer( Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public List<Answer> getAnswersList() {

        return (List<Answer>) answerRepository.findAll();
    }


    @Override
    public Answer getAnswers(long answerId) {
        Optional<Answer> answer = answerRepository.findById(answerId);
        if (answer.isPresent()) answer.get();
        return answer.get();
    }
}
