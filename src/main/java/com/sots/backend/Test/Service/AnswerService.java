package com.sots.backend.Test.Service;

import com.sots.backend.Test.Model.Answer;
import com.sots.backend.Test.Repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {
    @Autowired
    private AnswerRepository answerRepository;

    public List<Answer> saveAll(List<Answer> answers){
        return answerRepository.saveAll(answers);
    }
}
