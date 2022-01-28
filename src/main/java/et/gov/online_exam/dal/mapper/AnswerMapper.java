package et.gov.online_exam.dal.mapper;

import et.gov.online_exam.dal.dto.AnswerDto;
import et.gov.online_exam.dal.dto.RoleDto;
import et.gov.online_exam.dal.dto.UserDto;
import et.gov.online_exam.dal.entity.Answer;
import et.gov.online_exam.dal.entity.Role;
import et.gov.online_exam.dal.entity.User;
import org.mapstruct.Mapper;

import java.util.Collection;

@Mapper
public interface AnswerMapper {

    Answer toAnswer(AnswerDto answerDto);
    AnswerDto toAnswerDto(Answer answer);
}
