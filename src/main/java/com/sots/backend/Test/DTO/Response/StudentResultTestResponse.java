package com.sots.backend.Test.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentResultTestResponse {
    private Long resultId;
    private Long studentId;
    private String studentName;
    private ResultTestResponse test;
}
