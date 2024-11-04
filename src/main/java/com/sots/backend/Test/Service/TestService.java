package com.sots.backend.Test.Service;

import com.sots.backend.Test.Mapper.AnswerMapper;
import com.sots.backend.Test.Model.Answer;
import com.sots.backend.Test.Model.Question;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.Test.Repository.AnswerRepository;
import com.sots.backend.Test.Repository.QuestionRepository;
import com.sots.backend.Test.Repository.TestRepository;
import com.sots.backend.User.DTO.UserRegistrationDTO;
import com.sots.backend.User.Model.Role;
import com.sots.backend.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionRepository questionRepository;

    public Test createTest(Test test, List<Question> questions, List<Answer> answers) {
        Test savedTest = testRepository.save(test);
        List<Question> savedQuestions = questionRepository.saveAll(linkTestToQuestions(questions, savedTest));
        linkQuestionToAnswer(answers, savedQuestions);
        List<Answer> savedAnswers = answerRepository.saveAll(answers);
        return testRepository.findById(savedTest.getId()).orElseThrow();
    }

    private List<Question> linkTestToQuestions(List<Question> questions, Test t){
        List<Question> retList = new ArrayList<>();
        for(Question q : questions){
            q.setTest(t);
            retList.add(q);
        }
        return retList;
    }

    private void linkQuestionToAnswer(List<Answer> answers, List<Question> questions){
        for(Question q : questions){
            for(Answer a: answers){
                if(a.getQuestion().getQuestionText().equals(q.getQuestionText())){
                    a.setQuestion(q);
                }
            }
        }
    }
}
