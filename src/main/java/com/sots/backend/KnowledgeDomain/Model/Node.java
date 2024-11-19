package com.sots.backend.KnowledgeDomain.Model;

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
    private String label;
    private double x;
    private double y; //    Koordinate na frontu

    @OneToMany(mappedBy = "node")
    private List<Question> questionsInNode;

    @ManyToOne
    @JoinColumn(name = "knowledge_domain_id")
    private KnowledgeDomain knowledgeDomain;

    //Da li dodati listu veza (kako bi se znalo sa kojim cvorovima je povezano)


    public void setLabel(String label) {
        this.label = label;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setKnowledgeDomain(KnowledgeDomain knowledgeDomain) {
        this.knowledgeDomain = knowledgeDomain;
    }

    public KnowledgeDomain getKnowledgeDomain() {
        return knowledgeDomain;
    }
}