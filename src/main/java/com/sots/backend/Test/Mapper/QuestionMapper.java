package com.sots.backend.Test.Mapper;

import com.sots.backend.Test.DTO.QuestionDTO;
import com.sots.backend.Test.Model.Question;
import com.sots.backend.Test.Model.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class QuestionMapper {
    @Autowired
    private AnswerMapper answerMapper;
    public List<Question> questionDtoToList(List<QuestionDTO> dtoList){
        List<Question> retList = new ArrayList<>();
        for(QuestionDTO q : dtoList){
            retList.add(Question.builder().questionText(q.getQuestionText()).build());
        }
        return retList;
    }
}
