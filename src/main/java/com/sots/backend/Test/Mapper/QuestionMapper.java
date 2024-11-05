package com.sots.backend.Test.Mapper;

import com.sots.backend.Test.DTO.Request.QuestionRequest;
import com.sots.backend.Test.Model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class QuestionMapper {
    @Autowired
    private AnswerMapper answerMapper;
    public List<Question> questionDtoToList(List<QuestionRequest> dtoList){
        List<Question> retList = new ArrayList<>();
        for(QuestionRequest q : dtoList){
            retList.add(Question.builder().questionText(q.getQuestionText()).build());
        }
        return retList;
    }
}
