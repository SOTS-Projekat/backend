package com.sots.backend.Authentication;

import com.sots.backend.User.DTO.UserLoginDTO;
import com.sots.backend.User.DTO.UserRegistrationDTO;
import com.sots.backend.User.Model.User;
import com.sots.backend.User.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private  UserService userService;

    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private  JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
            User user = userService.registerNewUser(registrationDTO);
            return ResponseEntity.ok("User registered successfully with username: " + user.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO loginDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );

            User user = userService.findByUsername(loginDTO.getUsername());
            System.out.println(user);
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().toString());
            return ResponseEntity.ok(Map.of("jwt", token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
