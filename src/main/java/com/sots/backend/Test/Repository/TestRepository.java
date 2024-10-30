package com.sots.backend.Test.Repository;

import com.sots.backend.Test.Model.Test;
import com.sots.backend.User.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
}
