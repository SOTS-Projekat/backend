package com.sots.backend.Test.Service;

import com.sots.backend.Test.Model.Test;
import com.sots.backend.Test.Repository.TestRepository;
import com.sots.backend.User.DTO.UserRegistrationDTO;
import com.sots.backend.User.Model.Role;
import com.sots.backend.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;

    public Test registerNewUser(Test test) {
        Test test1 = new Test();
        return test1;
    }
}
