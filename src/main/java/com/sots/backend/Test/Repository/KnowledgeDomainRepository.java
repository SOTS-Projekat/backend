package com.sots.backend.Test.Repository;

import com.sots.backend.Test.Model.KnowledgeDomain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeDomainRepository extends JpaRepository<KnowledgeDomain, Long> {
}
