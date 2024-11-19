package com.sots.backend.KnowledgeDomain.Controller;

import com.sots.backend.KnowledgeDomain.DTO.KnowledgeDomainRequest;
import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import com.sots.backend.KnowledgeDomain.Service.KnowledgeDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/knowledgeDomain")
public class KnowledgeDomainController {

    @Autowired
    private KnowledgeDomainService knowledgeDomainService;

    @PostMapping("/create")
    public KnowledgeDomain createDomain(@RequestBody KnowledgeDomainRequest request) {
        return knowledgeDomainService.createEmptyKnowledgeDomain(request.getName(), request.getProfessorUsername());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteKnowledgeDomain(@PathVariable Long id) {
        knowledgeDomainService.deleteKnowledgeDomain(id);
    }


}
