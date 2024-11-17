package com.sots.backend.Test.Controller;

import com.sots.backend.Test.DTO.Request.NodeRequest;
import com.sots.backend.Test.Model.KnowledgeDomain;
import com.sots.backend.Test.Model.Node;
import com.sots.backend.Test.Service.KnowledgeDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/node")
public class NodeController {

    @Autowired
    private KnowledgeDomainService knowledgeDomainService;

    @PostMapping("/create")
    public Node createNode(@RequestBody NodeRequest nodeDTO, @RequestParam Long knowledgeDomainId) {

        KnowledgeDomain domain = knowledgeDomainService.findKnowledgeDomainById(knowledgeDomainId);

        return knowledgeDomainService.createNode(nodeDTO.getLabel(), nodeDTO.getX(), nodeDTO.getY(), domain);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteNode(@PathVariable Long id) {
        knowledgeDomainService.deleteNode(id);
    }




}
