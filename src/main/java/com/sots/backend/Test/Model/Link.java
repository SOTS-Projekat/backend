package com.sots.backend.Test.Model;

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
    private String name;
    private String source;
    private String target;  //  Kako bismo znali od kog do kog cvora je veza, odnosno kako je okrenuta

    @ManyToOne
    @JoinColumn(name = "knowledge_domain_id", nullable = false)
    private KnowledgeDomain knowledgeDomain;

    public void setName(String name) {
        this.name = name;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setKnowledgeDomain(KnowledgeDomain knowledgeDomain) {
        this.knowledgeDomain = knowledgeDomain;
    }
}
