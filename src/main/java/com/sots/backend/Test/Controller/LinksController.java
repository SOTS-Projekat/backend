package com.sots.backend.Test.Controller;

import com.sots.backend.Test.DTO.Request.LinkRequest;
import com.sots.backend.Test.Model.KnowledgeDomain;
import com.sots.backend.Test.Model.Link;
import com.sots.backend.Test.Service.KnowledgeDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
public class LinksController {

    @Autowired
    private KnowledgeDomainService knowledgeDomainService;

    @PostMapping("/create")
    public Link createLink(@RequestBody LinkRequest linkDTO, @RequestParam Long knowledgeDomainId) {
        KnowledgeDomain domain = knowledgeDomainService.findKnowledgeDomainById(knowledgeDomainId);
        return knowledgeDomainService.createLink(linkDTO.getSource(), linkDTO.getTarget(), domain);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteLink(@PathVariable Long id) {
        knowledgeDomainService.deleteLink(id);
    }


}
