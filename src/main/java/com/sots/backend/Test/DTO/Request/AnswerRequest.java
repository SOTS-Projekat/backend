package com.sots.backend.Test.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerRequest {
    private String answerText;

    @JsonProperty("isCorrect")  //dodali smo ovo posto za boolean se automatski gleda da isCorrect = correct, pa smo eksplicitno naveli da hocemo da vezemo ovako
    private boolean isCorrect;
}
