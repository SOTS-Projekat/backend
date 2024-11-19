package com.sots.backend.KnowledgeDomain.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkResponse {
    private Long id;
    private String label;
    private NodeResponse targetNode;
    private NodeResponse sourceNode;
}
