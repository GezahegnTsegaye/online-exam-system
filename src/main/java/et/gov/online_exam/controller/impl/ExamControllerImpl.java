package et.gov.online_exam.controller.impl;

import et.gov.online_exam.controller.ExamController;
import et.gov.online_exam.dal.entity.ExamReviewer;
import et.gov.online_exam.dal.repository.ExamReviewerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
