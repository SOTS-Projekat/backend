package com.sots.backend.Test.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultQuestionResponse {
    private Long id;
    private String questionText;
    private Long testId;
    private List<ResultAnswerResponse> offeredAnswers;
}
