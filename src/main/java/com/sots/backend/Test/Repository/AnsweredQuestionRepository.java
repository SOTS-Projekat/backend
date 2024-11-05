package com.sots.backend.Test.Repository;

import com.sots.backend.Test.Model.AnsweredQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnsweredQuestionRepository extends JpaRepository<AnsweredQuestion, Long> {
    List<AnsweredQuestion> findByResultId(Long resultId);
}
