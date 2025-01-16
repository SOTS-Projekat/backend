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

    public Optional<Test> getById(Long id){
        return testRepository.findById(id);
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

    public List<Test> getAll() {
        return testRepository.findAll();
    }

    public String generateQTIXml(Test test) {
        StringBuilder xmlBuilder = new StringBuilder();

        // Početak XML fajla
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<assessmentTest xmlns=\"http://www.imsglobal.org/xsd/imsqti_v2p1\" identifier=\"test_" + test.getId() + "\" title=\"" + test.getTitle() + "\">\n");

        for (Question question : test.getQuestions()) {
            xmlBuilder.append("  <assessmentItem identifier=\"q_" + question.getId() + "\" title=\"" + question.getQuestionText() + "\" adaptive=\"false\" timeDependent=\"false\">\n");
            xmlBuilder.append("    <itemBody>\n");
            xmlBuilder.append("      <choiceInteraction responseIdentifier=\"RESPONSE_" + question.getId() + "\" shuffle=\"true\" maxChoices=\"1\">\n");
            xmlBuilder.append("        <prompt>" + question.getQuestionText() + "</prompt>\n");

            for (Answer answer : question.getOfferedAnswers()) {
                xmlBuilder.append("        <simpleChoice identifier=\"a_" + answer.getId() + "\">" + answer.getAnswerText() + "</simpleChoice>\n");
            }

            xmlBuilder.append("      </choiceInteraction>\n");
            xmlBuilder.append("    </itemBody>\n");
            xmlBuilder.append("    <responseDeclaration identifier=\"RESPONSE_" + question.getId() + "\" cardinality=\"single\" baseType=\"identifier\">\n");
            xmlBuilder.append("      <correctResponse>\n");

            for (Answer answer : question.getOfferedAnswers()) {
                if (answer.isCorrect()) {
                    xmlBuilder.append("        <value>a_" + answer.getId() + "</value>\n");
                }
            }

            xmlBuilder.append("      </correctResponse>\n");
            xmlBuilder.append("    </responseDeclaration>\n");
            xmlBuilder.append("  </assessmentItem>\n");
        }

        // Kraj XML fajla
        xmlBuilder.append("</assessmentTest>\n");

        return xmlBuilder.toString();
    }

    public boolean deleteTestById(Long id) {
        if (testRepository.existsById(id)) {
            testRepository.deleteById(id); // Brisanje testa iz baze
            return true;
        } else {
            return false; // Test sa datim ID-jem ne postoji
        }
    }

}
