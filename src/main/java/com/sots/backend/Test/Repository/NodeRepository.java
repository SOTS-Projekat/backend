package com.sots.backend.Test.Repository;

import com.sots.backend.Test.Model.Node;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<Node, Long> {
}
