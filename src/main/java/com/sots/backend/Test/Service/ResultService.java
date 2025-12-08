package com.sots.backend.Test.Service;

import com.sots.backend.Test.DTO.Request.AnsweredQuestionRequest;
import com.sots.backend.Test.DTO.Request.ResultRequest;
import com.sots.backend.Test.DTO.Response.ResultTestResponse;
import com.sots.backend.Test.DTO.Response.StudentResultTestResponse;
import com.sots.backend.Test.Mapper.TestMapper;
import com.sots.backend.Test.Model.*;
import com.sots.backend.Test.Repository.*;
import com.sots.backend.User.Model.User;
import com.sots.backend.User.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private AnsweredQuestionRepository answeredQuestionRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TestMapper testMapper;

    public ResultTestResponse save(ResultRequest resultRequest){

        User user = userRepository.findById(resultRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Test test = testRepository.findById(resultRequest.getTestId())
                .orElseThrow(() -> new RuntimeException("Test not found"));


        Result result = Result.builder()
                .score(resultRequest.getScore())
                .test(test)
                .student(user)
                .dateAndTime(LocalDateTime.now())
                .build();

        Result savedResult =  resultRepository.save(result);

        List<AnsweredQuestion> answeredQuestions = mapAnsweredQuestions(resultRequest.getAnsweredQuestions(), savedResult);
        List<AnsweredQuestion> savedAnsweredQuestions = answeredQuestionRepository.saveAll(answeredQuestions);

        Result returnResult = resultRepository.findByIdWithDetails(savedResult.getId()).get();
        List<AnsweredQuestion> answeredQuestionList = answeredQuestionRepository.findByResultId(returnResult.getId());
        return testMapper.toResultTestResponse(returnResult.getTest(), answeredQuestionList);

    }

    //  Uzmemo listu iz repo i mapiramo na response
    public ResultTestResponse getResultTestResponse(Long studentId, Long testId){
        Result returnResult = resultRepository.findByStudentIdAndTestIdWithDetails(studentId, testId).get();
        List<AnsweredQuestion> answeredQuestionList = answeredQuestionRepository.findByResultId(returnResult.getId());
        return testMapper.toResultTestResponse(returnResult.getTest(), answeredQuestionList);
    }


    private List<AnsweredQuestion> mapAnsweredQuestions(List<AnsweredQuestionRequest> answeredQuestionRequests, Result result) {
        List<AnsweredQuestion> retList = new ArrayList<>();
        for (AnsweredQuestionRequest request : answeredQuestionRequests) {
            Question question = questionRepository.findById(request.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found with ID: " + request.getQuestionId()));

            Answer answer = null;
            if (request.getAnswerId() != null) {
                answer = answerRepository.findById(request.getAnswerId())
                        .orElseThrow(() -> new RuntimeException("Answer not found with ID: " + request.getAnswerId()));
            }

            retList.add(AnsweredQuestion.builder()
                    .result(result)
                    .question(question)
                    .selectedAnswer(answer)
                    .build());
        }
        return retList;
    }

    public List<Result> getAllResultsByStudent(Long studentId){
        return resultRepository.findByStudentId(studentId);
    }


    public List<Result> getResultsByTestId(Long testId) {return resultRepository.findAllByTestId(testId); }
}
