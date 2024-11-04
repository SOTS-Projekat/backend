package com.sots.backend.Test.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTestDTO {
    private String title;
    private List<QuestionDTO> questions;
    private Long proffesorId;
}
