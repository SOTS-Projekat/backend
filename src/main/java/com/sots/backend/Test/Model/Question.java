package com.sots.backend.Test.Model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    //korekcija
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> offeredAnswers;
}

