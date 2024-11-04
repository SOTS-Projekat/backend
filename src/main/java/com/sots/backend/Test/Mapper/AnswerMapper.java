package com.sots.backend.Test.Mapper;

import com.sots.backend.Test.DTO.AnswerDTO;
import com.sots.backend.Test.DTO.QuestionDTO;
import com.sots.backend.Test.Model.Answer;
import com.sots.backend.Test.Model.Question;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class AnswerMapper {

    public List<Answer> answerDtoToList(List<QuestionDTO> dtoList){
        List<Answer> retList = new ArrayList<>();
        for(QuestionDTO q : dtoList){
            for(AnswerDTO a: q.getOfferedAnswers()){
                retList.add(Answer.builder().answerText(a.getAnswerText()).isCorrect(a.isCorrect()).question(Question.builder().questionText(q.getQuestionText()).build()).build());
            }
        }
        return retList;
    }
}
