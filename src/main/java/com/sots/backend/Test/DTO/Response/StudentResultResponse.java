package com.sots.backend.Test.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentResultResponse {
    private Long studentId;
    private String studentName;
    private ResultTestResponse result;
}
