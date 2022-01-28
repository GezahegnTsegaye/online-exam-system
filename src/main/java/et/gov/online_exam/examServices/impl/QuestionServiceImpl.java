package et.gov.online_exam.examServices.impl;


import et.gov.online_exam.dal.dto.QuestionDto;
import et.gov.online_exam.dal.entity.Question;
import et.gov.online_exam.dal.mapper.QuestionMapper;
import et.gov.online_exam.dal.repository.ExamQuestionRepository;
import et.gov.online_exam.dal.repository.QuestionRepository;
import et.gov.online_exam.examServices.QuestionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {



    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionRepository questionRepository, ExamQuestionRepository examQuestionRepository, QuestionMapper questionMapper) {
        this.questionRepository = questionRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.questionMapper = questionMapper;
    }

    public Question insertQuestion(QuestionDto questionDto) {

        Optional<Question> questions = questionRepository.findById(questionDto.getQuestionId());
        Question question = new Question();
        if(questions.isPresent()){
            question = questionMapper.toQuestion(questionDto);
        }
        return question;
    }
}
