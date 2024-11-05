package com.sots.backend.Test.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sots.backend.User.Model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    //@JsonIgnoreProperties({"questionText", "test"})
    private Question question;

    private String answerText;

    private boolean isCorrect;
}
