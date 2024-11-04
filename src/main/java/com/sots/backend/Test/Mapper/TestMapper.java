package com.sots.backend.Test.Mapper;

import com.sots.backend.Test.DTO.CreateTestDTO;
import com.sots.backend.Test.Model.Question;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.User.Model.User;
import com.sots.backend.User.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestMapper {

    @Autowired
    private UserService userService;
    @Autowired
    private QuestionMapper questionMapper;
    public Test createTestDTOtoEntity(CreateTestDTO dto){
        //Promjeni proffesor id
        User professor = userService.findById(1L);
        return Test.builder().title(dto.getTitle()).professor(professor)
        .build();
    }
}
