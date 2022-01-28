package et.gov.online_exam.dal.mapper;


import et.gov.online_exam.dal.dto.QuestionDto;
import et.gov.online_exam.dal.entity.Question;
import org.mapstruct.Mapper;

@Mapper
public interface QuestionMapper {
    Question toQuestion(QuestionDto questionDto);
    QuestionDto toQuestionDto(Question question);
}
