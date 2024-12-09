package com.sots.backend.Test.Repository;

import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.User.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    @Query("SELECT t.knowledgeDomain FROM Test t WHERE t.id = :testId")
    KnowledgeDomain findKnowledgeDomainByTestId(Long testId);
}
