package com.sots.backend.Test.Controller;

import com.sots.backend.Test.DTO.CreateTestDTO;
import com.sots.backend.Test.Mapper.AnswerMapper;
import com.sots.backend.Test.Mapper.QuestionMapper;
import com.sots.backend.Test.Mapper.TestMapper;
import com.sots.backend.Test.Model.Answer;
import com.sots.backend.Test.Model.Question;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.Test.Service.TestService;
import com.sots.backend.User.DTO.UserRegistrationDTO;
import com.sots.backend.User.Model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    private TestService testService;
    @Autowired
    private TestMapper testMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private AnswerMapper answerMapper;

    @PostMapping("")
    public ResponseEntity<Test> createTest(@RequestBody CreateTestDTO test) {
        Test t = testMapper.createTestDTOtoEntity(test);
        List<Question> questions = questionMapper.questionDtoToList(test.getQuestions());
        List<Answer> answers = answerMapper.answerDtoToList(test.getQuestions());
        return ResponseEntity.ok(testService.createTest(t, questions, answers));
    }
}
