package com.sots.backend.User.Service;

import com.sots.backend.User.DTO.UserRegistrationDTO;
import com.sots.backend.User.Model.Role;
import com.sots.backend.User.Model.User;
import com.sots.backend.User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService  {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())){
            throw new IllegalArgumentException("Username already taken");
        }

        if (userRepository.existsByEmail(registrationDTO.getEmail())){
            throw new IllegalArgumentException("Email already taken");
        }

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRole(Role.PROFESSOR); // Automatically set as PROFESSOR
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<User> getAllStudents() {
        return userRepository.findAllByRole(Role.STUDENT);
    }
}
