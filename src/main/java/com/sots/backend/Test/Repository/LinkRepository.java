package com.sots.backend.Test.Repository;

import com.sots.backend.Test.Model.Link;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkRepository extends JpaRepository<Link, Long> {
}
