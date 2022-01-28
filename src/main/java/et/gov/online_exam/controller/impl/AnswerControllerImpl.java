package et.gov.online_exam.controller.impl;


import et.gov.online_exam.controller.AnswerController;
import et.gov.online_exam.dal.dto.AnswerDto;
import et.gov.online_exam.dal.entity.Answer;
import et.gov.online_exam.dal.mapper.AnswerMapper;
import et.gov.online_exam.dal.mapper.UserMapper;
import et.gov.online_exam.examServices.AnswerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(exposedHeaders = "errors, content-type")
@RequestMapping(name = "api/v2")
public class AnswerControllerImpl implements AnswerController {


    private final AnswerService answerService;
    private final UserMapper userMapper;
    private final AnswerMapper answerMapper;

    public AnswerControllerImpl(AnswerService answerService, UserMapper userMapper, AnswerMapper answerMapper) {
        this.answerService = answerService;
        this.userMapper = userMapper;
        this.answerMapper = answerMapper;
    }


    @PreAuthorize("hasRole(@roles.ADMIN")
    @PostMapping(value = "/users")
    public ResponseEntity<AnswerDto> insertAnswer(@RequestBody @Valid AnswerDto answerDto) {
        Answer answer = answerMapper.toAnswer(answerDto);
        this.answerService.insertAnswer(answer);
        return new ResponseEntity<AnswerDto>(answerMapper.toAnswerDto(answer), HttpStatus.CREATED);
    }

}
