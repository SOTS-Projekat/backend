package com.sots.backend.Test.Repository;

import com.sots.backend.Test.Model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    @Query("SELECT r FROM Result r " +
            "JOIN FETCH r.student " +
            "JOIN FETCH r.test " +
            "LEFT JOIN FETCH r.answeredQuestions aq " +
            "LEFT JOIN FETCH aq.selectedAnswer " +
            "LEFT JOIN FETCH aq.question " +
            "WHERE r.id = :id")
    Optional<Result> findByIdWithDetails(Long id);

    @Query("SELECT r FROM Result r WHERE r.test.id = :testId")
    List<Result> findAllByTestId(Long testId);
}
