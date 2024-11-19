package com.sots.backend.KnowledgeDomain.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "linksBetweenNodes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;

    @ManyToOne
    @JoinColumn(name = "source_node_id", nullable = false)
    private Node sourceNode;

    @ManyToOne
    @JoinColumn(name = "target_node_id", nullable = false)
    private Node targetNode;

    @ManyToOne
    @JoinColumn(name = "knowledge_domain_id", nullable = false)
    @JsonBackReference // Sprečava beskonačno ugnježđavanje
    private KnowledgeDomain knowledgeDomain;
}
