package com.sots.backend.User.Repository;

import com.sots.backend.User.Model.Role;
import com.sots.backend.User.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAllByRole(Role role);
    Optional<User> findByUsernameAndRole(String username, Role role);
}
