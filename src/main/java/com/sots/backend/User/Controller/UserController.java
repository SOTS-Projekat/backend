package com.sots.backend.User.Controller;

import com.sots.backend.User.Model.User;
import com.sots.backend.User.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/allStudents")     //For professor statistics later
    public ResponseEntity<?> getAllStudents(){
        List<User> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }
}
