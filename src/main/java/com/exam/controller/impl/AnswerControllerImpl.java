package com.exam.controller.impl;


import com.exam.controller.AnswerController;
import com.exam.dal.dto.AnswerDto;
import com.exam.dal.entity.Answer;
import com.exam.dal.mapper.AnswerMapper;
import com.exam.dal.mapper.UserMapper;
import com.exam.service.AnswerService;
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
