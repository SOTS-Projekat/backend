package com.sots.backend.KnowledgeDomain.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeDomainRequest {

    private String name;
    private String professorId;
    private String description;
    private List<NodeRequest> nodes;
    private List<LinkRequest> links;
}
