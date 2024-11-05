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
public class TestResponse {
    private Long id;
    private String title;
    private List<QuestionResponse> questions;
}
