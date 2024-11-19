package com.sots.backend.KnowledgeDomain.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeResponse {
    private Long id;
    private String label;
    private String frontendId;
}
