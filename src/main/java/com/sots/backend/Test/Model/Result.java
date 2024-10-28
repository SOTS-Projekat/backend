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
    private User student;

    @OneToMany
    private List<Question> incorrectAnswers;
}

