package com.sots.backend.Test.Controller;

import com.sots.backend.Test.DTO.Request.CreateTestRequest;
import com.sots.backend.Test.DTO.Response.TestResponse;
import com.sots.backend.Test.Mapper.AnswerMapper;
import com.sots.backend.Test.Mapper.QuestionMapper;
import com.sots.backend.Test.Mapper.TestMapper;
import com.sots.backend.Test.Model.Answer;
import com.sots.backend.Test.Model.Question;
import com.sots.backend.Test.Model.Result;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.Test.Service.ResultService;
import com.sots.backend.Test.Service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    private TestService testService;
    @Autowired
    private ResultService resultService;
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
        return ResponseEntity.ok(testService.createTest(t, questions, answers, test.getProfessorId()));
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

    @GetMapping("/all")
    public ResponseEntity<List<TestResponse>> getAll() {
        List<Test> tests = testService.getAll();
        List<TestResponse> testResponses = tests.stream()
                .map(TestMapper::toTestResponse)
                .toList();

        return ResponseEntity.ok(testResponses);
    }

    @GetMapping("/all-for-student/{studentId}")
    public ResponseEntity<List<TestResponse>> getAllForStudent(@PathVariable Long studentId) {
        List<Test> tests = testService.getAll();
        List<TestResponse> testResponses = tests.stream()
                .map(TestMapper::toTestResponse)
                .toList();

        List<Result> studentResults = resultService.getAllResultsByStudent(studentId);

        for(Result sr : studentResults){
            for(TestResponse ts : testResponses){
                if(sr.getTest().getId() == ts.getId()){
                    ts.setSolved(true);
                }
            }
        }

        return ResponseEntity.ok(testResponses);
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<Resource> exportTestToQTI(@PathVariable Long id) {
        Optional<Test> optionalTest = testService.getById(id);

        if (optionalTest.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Test test = optionalTest.get();

        String qtiXml = testService.generateQTIXml(test);

        ByteArrayResource resource = new ByteArrayResource(qtiXml.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test_" + id + ".xml")
                .contentType(MediaType.APPLICATION_XML)
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        boolean isDeleted = testService.deleteTestById(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build(); // Uspešno obrisano
        } else {
            return ResponseEntity.notFound().build(); // Test nije pronađen
        }
    }

}
