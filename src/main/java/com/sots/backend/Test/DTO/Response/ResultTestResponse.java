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
public class ResultTestResponse {
    private Long id;
    private String title;
    private List<ResultQuestionResponse> questions;
}
