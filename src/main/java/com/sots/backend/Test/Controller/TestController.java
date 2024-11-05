package com.sots.backend.Test.Controller;

import com.sots.backend.Test.DTO.Request.CreateTestRequest;
import com.sots.backend.Test.DTO.Response.TestResponse;
import com.sots.backend.Test.Mapper.AnswerMapper;
import com.sots.backend.Test.Mapper.QuestionMapper;
import com.sots.backend.Test.Mapper.TestMapper;
import com.sots.backend.Test.Model.Answer;
import com.sots.backend.Test.Model.Question;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.Test.Service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Test> createTest(@RequestBody CreateTestRequest test) {
        Test t = testMapper.createTestDTOtoEntity(test);
        List<Question> questions = questionMapper.questionDtoToList(test.getQuestions());
        List<Answer> answers = answerMapper.answerDtoToList(test.getQuestions());
        return ResponseEntity.ok(testService.createTest(t, questions, answers));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TestResponse> getById(@PathVariable Long id) {
        Optional<Test> test = testService.getById(id);

        if (test.isPresent()) {
            TestResponse testDTO = TestMapper.toTestResponse(test.get());
            return ResponseEntity.ok(testDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
