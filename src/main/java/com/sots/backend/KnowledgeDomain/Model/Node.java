package com.sots.backend.KnowledgeDomain.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sots.backend.Test.Model.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String frontendId;

    private String label;

    @ManyToOne
    @JoinColumn(name = "knowledge_domain_id")
    @JsonBackReference // Sprečava beskonačno ugnježđavanje
    private KnowledgeDomain knowledgeDomain;
}
