package com.sots.backend.KnowledgeDomain.Repository;

import com.sots.backend.KnowledgeDomain.Model.Link;
import com.sots.backend.KnowledgeDomain.Model.Node;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {

    boolean existsBySourceNodeAndTargetNode(Node sourceNode, Node targetNode);  //  Provera da li postoji vec veza sa istim node za isti pravac
}
