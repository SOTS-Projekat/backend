package com.sots.backend.KnowledgeDomain.DTO.Response;

import com.sots.backend.User.Model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeDomainResponse {
    private Long id;
    private String name;
    private String description;
    private UserResponse professor;
    private List<LinkResponse> links;
    private List<NodeResponse> nodes;
    private LocalDate date;
}
