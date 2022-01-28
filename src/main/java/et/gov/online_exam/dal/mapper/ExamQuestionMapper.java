package et.gov.online_exam.dal.mapper;


import et.gov.online_exam.dal.dto.ExamQuestionDto;
import et.gov.online_exam.dal.entity.ExamQuestion;
import org.mapstruct.Mapper;

@Mapper
public interface ExamQuestionMapper {

    ExamQuestion toExamQuestion(ExamQuestionDto examQuestionDto);
ExamQuestionDto toExamQuestionDto(ExamQuestion examQuestion);
}
