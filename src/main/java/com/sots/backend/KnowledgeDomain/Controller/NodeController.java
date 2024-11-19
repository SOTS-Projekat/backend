package com.sots.backend.KnowledgeDomain.Controller;

import com.sots.backend.KnowledgeDomain.DTO.NodeRequest;
import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import com.sots.backend.KnowledgeDomain.Model.Node;
import com.sots.backend.KnowledgeDomain.Service.KnowledgeDomainService;
import com.sots.backend.KnowledgeDomain.Service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/node")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    @PostMapping("/create")
    public Node createNode(@RequestBody NodeRequest nodeDTO) {
        return nodeService.createNode(nodeDTO.getLabel(), nodeDTO.getX(), nodeDTO.getY(), nodeDTO.getKnowledgeDomainId());
    }

    @DeleteMapping("/delete/{id}")
    public void deleteNode(@PathVariable Long id) {
        nodeService.deleteNode(id);
    }

}
