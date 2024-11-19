package com.sots.backend.KnowledgeDomain.Model;

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

    @ManyToOne  //  Posto svaki link ima 2 node, pocetni i krajnji
    @JoinColumn(name = "source_node_id", nullable = false)
    private Node sourceNode;

    @ManyToOne
    @JoinColumn(name = "target_node_id", nullable = false)
    private Node targetNode;

    @ManyToOne
    @JoinColumn(name = "knowledge_domain_id", nullable = false)
    private KnowledgeDomain knowledgeDomain;    //  Ovo je verovatno nepotrebno obzirom da ima ime dva cvora koja povezuje, pa moze od njih uzeti u kom se prostoru nalazi

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    public void setKnowledgeDomain(KnowledgeDomain knowledgeDomain) {
        this.knowledgeDomain = knowledgeDomain;
    }
}
