package com.sots.backend.Test.Model;

import com.sots.backend.User.Model.User;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import java.util.List;

@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    private int score;

    private int totalQuestions;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL)
    private List<AnsweredQuestion> answeredQuestions;
}

