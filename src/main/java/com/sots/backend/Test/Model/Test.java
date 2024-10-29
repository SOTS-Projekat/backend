package com.sots.backend.Test.Model;

import com.sots.backend.User.Model.User;
import jakarta.persistence.*;

import java.util.List;

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
}
