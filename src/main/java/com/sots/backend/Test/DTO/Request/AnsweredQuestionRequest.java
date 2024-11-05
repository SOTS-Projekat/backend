package com.sots.backend.Test.DTO.Request;

import com.sots.backend.Test.Model.Answer;
import com.sots.backend.Test.Model.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnsweredQuestionRequest {
//    private Question question;
//    private Answer selectedAnswer;
    private Long questionId;
    private Long answerId;
    //private boolean isCorrect;
}
