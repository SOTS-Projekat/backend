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
public class CreateTestRequest {
    private String title;
    private List<QuestionRequest> questions;
    private Long knowledgeDomainId;
    private Long professorId;
}
