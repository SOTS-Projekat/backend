package com.sots.backend.Test.Controller;

import com.sots.backend.Test.DTO.Request.ResultRequest;
import com.sots.backend.Test.DTO.Response.ResultTestResponse;
import com.sots.backend.Test.DTO.Response.StudentResultResponse;
import com.sots.backend.Test.Model.*;
import com.sots.backend.Test.Service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/result")
public class ResultController {
    @Autowired
    private ResultService resultService;

    @PostMapping("")
    public ResponseEntity<ResultTestResponse> save(@RequestBody ResultRequest resultRequest) {
        return ResponseEntity.ok(resultService.save(resultRequest));
    }

    @GetMapping("/{studentId}/{testId}")
    public ResponseEntity<ResultTestResponse> getById(@PathVariable Long studentId, @PathVariable Long testId) {
        ResultTestResponse resultTestResponse = resultService.getResultTestResponse(studentId, testId);
        return ResponseEntity.ok(resultTestResponse);
    }

    @GetMapping("/{testId}/results")
    public ResponseEntity<List<StudentResultResponse>> getByTestId(@PathVariable Long testId) {
        return ResponseEntity.ok(resultService.getResultsByTestId(testId));
    }


}
