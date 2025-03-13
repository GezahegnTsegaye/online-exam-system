package com.exam.controller.impl;

import com.exam.controller.ExamController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1")
public class ExamControllerImpl implements ExamController {


   /* @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamReviewerRepository examReviewerRepository;


    @PostMapping(value = "/insertExam")
    public Exam newExam(@RequestBody Exam exam){
        return examRepository.save(exam);
    }

    @GetMapping(value = "/exams")
    public List<Exam> getAllExam() {
        return (List<Exam>) examRepository.findAll();
    }


    @PostMapping(value = "/insertExamReview")
    public ExamReviewer newExamReviewer(@RequestBody ExamReviewer examReviewer){
        return examReviewerRepository.save(examReviewer);
    }*/
}
