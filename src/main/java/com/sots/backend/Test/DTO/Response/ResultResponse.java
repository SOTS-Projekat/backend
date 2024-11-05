package com.sots.backend.Test.DTO.Response;

import com.sots.backend.User.Model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultResponse {
    private Long id;
    private User student;
    private TestResponse testResponse;
}
