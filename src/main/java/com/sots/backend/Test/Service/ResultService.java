package com.sots.backend.Test.Service;

import com.sots.backend.Test.DTO.Request.AnsweredQuestionRequest;
import com.sots.backend.Test.DTO.Request.ResultRequest;
import com.sots.backend.Test.DTO.Response.ResultTestResponse;
import com.sots.backend.Test.Mapper.TestMapper;
import com.sots.backend.Test.Model.*;
import com.sots.backend.Test.Repository.AnsweredQuestionRepository;
import com.sots.backend.Test.Repository.ResultRepository;
import com.sots.backend.Test.Repository.TestRepository;
import com.sots.backend.User.Model.User;
import com.sots.backend.User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private AnsweredQuestionRepository answeredQuestionRepository;
    @Autowired
    private UserRepository userRepository;
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

    public ResultTestResponse getResultTestResponse(Long id){
        Result returnResult = resultRepository.findByIdWithDetails(id).get();
        List<AnsweredQuestion> answeredQuestionList = answeredQuestionRepository.findByResultId(returnResult.getId());
        return testMapper.toResultTestResponse(returnResult.getTest(), answeredQuestionList);
    }

    private List<AnsweredQuestion> mapAnsweredQuestions(List<AnsweredQuestionRequest> answeredQuestionRequests, Result result){
        List<AnsweredQuestion> retList = new ArrayList<>();
        for(AnsweredQuestionRequest a : answeredQuestionRequests){
            if(a.getAnswerId() != null){
                retList.add(AnsweredQuestion.builder()
                        .question(Question.builder().id(a.getQuestionId()).build())
                        .result(result)
                        .selectedAnswer(Answer.builder().id(a.getAnswerId()).build())
                        .build());
            }else{
                retList.add(AnsweredQuestion.builder()
                        .question(Question.builder().id(a.getQuestionId()).build())
                        .result(result)
                        .selectedAnswer(null)
                        .build());
            }

        }
        return retList;
    }

}
