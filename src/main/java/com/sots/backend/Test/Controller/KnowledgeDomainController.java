package com.sots.backend.Test.Controller;

import com.sots.backend.Test.Model.KnowledgeDomain;
import com.sots.backend.Test.Service.KnowledgeDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledgeDomains")
public class KnowledgeDomainController {

    @Autowired
    private KnowledgeDomainService knowledgeDomainService;

    @PostMapping("/create")
    public KnowledgeDomain createDomain(@RequestParam String name) {
        return knowledgeDomainService.createEmptyKnowledgeDomain(name);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteKnowledgeDomain(@PathVariable Long id) {
        knowledgeDomainService.deleteKnowledgeDomain(id);
    }


}
