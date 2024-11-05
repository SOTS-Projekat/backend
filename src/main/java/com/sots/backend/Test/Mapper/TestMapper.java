package com.sots.backend.Test.Mapper;

import com.sots.backend.Test.DTO.Request.CreateTestRequest;
import com.sots.backend.Test.DTO.Response.AnswerResponse;
import com.sots.backend.Test.DTO.Response.QuestionResponse;
import com.sots.backend.Test.DTO.Response.TestResponse;
import com.sots.backend.Test.Model.Answer;
import com.sots.backend.Test.Model.Question;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.User.Model.User;
import com.sots.backend.User.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TestMapper {

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionMapper questionMapper;
    public Test createTestDTOtoEntity(CreateTestRequest dto){
        //Promjeni proffesor id
        User professor = userService.findById(1L);
        return Test.builder().title(dto.getTitle()).professor(professor)
        .build();
    }

    public static TestResponse toTestResponse(Test test) {
        TestResponse testDTO = new TestResponse();
        testDTO.setId(test.getId());
        testDTO.setTitle(test.getTitle());
        testDTO.setQuestions(test.getQuestions().stream()
                .map(TestMapper::toQuestionResponse)
                .collect(Collectors.toList()));

        return testDTO;
    }

    public static QuestionResponse toQuestionResponse(Question question) {
        QuestionResponse questionDTO = new QuestionResponse();
        questionDTO.setId(question.getId());
        questionDTO.setQuestionText(question.getQuestionText());
        questionDTO.setTestId(question.getTest().getId()); // Samo ID testa
        questionDTO.setOfferedAnswers(question.getOfferedAnswers().stream()
                .map(TestMapper::toAnswerResponse)
                .collect(Collectors.toList()));

        return questionDTO;
    }

    public static AnswerResponse toAnswerResponse(Answer answer) {
        AnswerResponse answerDTO = new AnswerResponse();
        answerDTO.setId(answer.getId());
        answerDTO.setAnswerText(answer.getAnswerText());
        answerDTO.setCorrect(answer.isCorrect());

        return answerDTO;
    }
}
