package com.sots.backend.KnowledgeDomain.Repository;

import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KnowledgeDomainRepository extends JpaRepository<KnowledgeDomain, Long> {

    @Query("SELECT COUNT(d) > 0 FROM KnowledgeDomain d WHERE LOWER(d.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    //boolean existsByName(String name);
}
