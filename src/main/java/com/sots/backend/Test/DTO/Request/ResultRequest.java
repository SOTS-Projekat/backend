package com.sots.backend.Test.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultRequest {
    private Long testId;
    private Long userId;
    private double score;
    private List<AnsweredQuestionRequest> answeredQuestions;
}
