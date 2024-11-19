package com.sots.backend.KnowledgeDomain.Controller;

import com.sots.backend.KnowledgeDomain.Service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/link")
public class LinksController {

    @Autowired
    private LinkService linkService;

//    @PostMapping("/create")
//    public Link createLink(@RequestBody LinkRequest linkDTO) {
//        return linkService.createLink(linkDTO.getSourceNodeId(), linkDTO.getTargetNodeId());
//    }

    @DeleteMapping("/delete/{id}")
    public void deleteLink(@PathVariable Long id) {
        linkService.deleteLink(id);
    }

}
