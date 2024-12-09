package com.sots.backend.Test.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import com.sots.backend.User.Model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<Question> questions;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private User professor;

    @ManyToOne
    @JoinColumn(name = "knowledge_domain_id")
    @JsonIgnore
    private KnowledgeDomain knowledgeDomain;
}
