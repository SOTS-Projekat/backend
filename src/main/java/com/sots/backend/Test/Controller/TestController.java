package com.sots.backend.Test.Controller;

import com.sots.backend.Test.DTO.CreateTestDTO;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.Test.Service.TestService;
import com.sots.backend.User.DTO.UserRegistrationDTO;
import com.sots.backend.User.Model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Autowired
    private TestService testService;

    @PostMapping("")
    public ResponseEntity<Test> createTest(@RequestBody CreateTestDTO test) {
        Test t = new Test();
        return ResponseEntity.ok(t);
    }
}
