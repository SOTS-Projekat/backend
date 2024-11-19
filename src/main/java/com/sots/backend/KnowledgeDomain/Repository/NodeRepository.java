package com.sots.backend.KnowledgeDomain.Repository;

import com.sots.backend.KnowledgeDomain.Model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NodeRepository extends JpaRepository<Node, Long> {

    @Query("SELECT COUNT(n) > 0 FROM Node n WHERE LOWER(n.label) = LOWER(:label)")
    boolean existsByLabelIgnoreCase(@Param("label") String label);
}
