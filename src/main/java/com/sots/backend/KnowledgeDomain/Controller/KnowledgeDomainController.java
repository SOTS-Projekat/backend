package com.sots.backend.KnowledgeDomain.Controller;

import com.sots.backend.KnowledgeDomain.DTO.Request.KnowledgeDomainRequest;
import com.sots.backend.KnowledgeDomain.DTO.Response.KnowledgeDomainResponse;
import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import com.sots.backend.KnowledgeDomain.Service.KnowledgeDomainService;
import com.sots.backend.Test.DTO.Response.TestResponse;
import com.sots.backend.Test.Mapper.TestMapper;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/knowledgeDomain")
public class KnowledgeDomainController {

    @Autowired
    private KnowledgeDomainService knowledgeDomainService;

    @PostMapping("")
    public ResponseEntity<KnowledgeDomainResponse> createDomain(@RequestBody KnowledgeDomainRequest request) {
        KnowledgeDomainResponse knowledgeDomainResponse = knowledgeDomainService.save(request);
        return ResponseEntity.ok(knowledgeDomainResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<KnowledgeDomainResponse>> getAllKnowledgeDomains(@PathVariable Long id){
        List<KnowledgeDomainResponse> knowledgeDomainResponses = knowledgeDomainService.getAll(id);
        return ResponseEntity.ok(knowledgeDomainResponses);
    }

    @GetMapping("/getForTestCreation/{id}")
    public ResponseEntity<List<KnowledgeDomainResponse>> getAllKnowledgeDomainsForTestCreation(@PathVariable Long id){
        List<KnowledgeDomainResponse> knowledgeDomainResponses = knowledgeDomainService.getAll(id);
        return ResponseEntity.ok(knowledgeDomainResponses);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteKnowledgeDomain(@PathVariable Long id) {
        knowledgeDomainService.deleteKnowledgeDomain(id);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<KnowledgeDomainResponse> findById(@PathVariable Long id) {
            KnowledgeDomainResponse domain = knowledgeDomainService.findById(id);
            return ResponseEntity.ok(domain);
    }


}
