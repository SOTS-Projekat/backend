package com.sots.backend.Test.Controller;

import com.sots.backend.Test.DTO.Request.CreateTestRequest;
import com.sots.backend.Test.DTO.Request.ResultRequest;
import com.sots.backend.Test.DTO.Response.ResultTestResponse;
import com.sots.backend.Test.DTO.Response.TestResponse;
import com.sots.backend.Test.Mapper.TestMapper;
import com.sots.backend.Test.Model.Answer;
import com.sots.backend.Test.Model.Question;
import com.sots.backend.Test.Model.Result;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.Test.Service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/result")
public class ResultController {
    @Autowired
    private ResultService resultService;

    @PostMapping("")
    public ResponseEntity<ResultTestResponse> save(@RequestBody ResultRequest resultRequest) {
        return ResponseEntity.ok(resultService.save(resultRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultTestResponse> getById(@PathVariable Long id) {
        ResultTestResponse resultTestResponse = resultService.getResultTestResponse(id);
        return ResponseEntity.ok(resultTestResponse);
    }
}
