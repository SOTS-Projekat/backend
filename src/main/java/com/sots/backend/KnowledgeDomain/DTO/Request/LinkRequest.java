package com.sots.backend.KnowledgeDomain.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LinkRequest {
    private String name;
    private NodeRequest source;
    private NodeRequest target;
}
