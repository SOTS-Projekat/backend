package com.sots.backend.Test.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sots.backend.User.Model.User;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private double score;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL)
    private List<AnsweredQuestion> answeredQuestions;

    private LocalDateTime dateAndTime;
}

