package com.sots.backend.Test.Repository;

import com.sots.backend.Test.Model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
