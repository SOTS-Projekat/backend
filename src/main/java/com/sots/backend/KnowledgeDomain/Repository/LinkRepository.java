package com.sots.backend.KnowledgeDomain.Repository;

import com.sots.backend.KnowledgeDomain.Model.Link;
import com.sots.backend.KnowledgeDomain.Model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LinkRepository extends JpaRepository<Link, Long> {

    boolean existsBySourceNodeAndTargetNode(Node sourceNode, Node targetNode);  //  Provera da li postoji vec veza sa istim node za isti pravac

    @Modifying
    @Query("DELETE FROM Link l WHERE l.knowledgeDomain.id = :domainId")
    void deleteLinksByKnowledgeDomainId(Long domainId);
}
