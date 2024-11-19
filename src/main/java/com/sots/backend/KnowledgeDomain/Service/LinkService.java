package com.sots.backend.KnowledgeDomain.Service;

import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import com.sots.backend.KnowledgeDomain.Model.Link;
import com.sots.backend.KnowledgeDomain.Model.Node;
import com.sots.backend.KnowledgeDomain.Repository.KnowledgeDomainRepository;
import com.sots.backend.KnowledgeDomain.Repository.LinkRepository;
import com.sots.backend.KnowledgeDomain.Repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkService {

    @Autowired
    LinkRepository linkRepository;

    @Autowired
    KnowledgeDomainRepository knowledgeDomainRepository;

    @Autowired
    NodeRepository nodeRepository;

    public Link createLink(Long sourceNodeId, Long targetNodeId) {

        Link link = new Link();

        Node sourceNode = nodeRepository.findById(sourceNodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node NOT FOUND with ID: " + sourceNodeId));
        Node targetNode = nodeRepository.findById(targetNodeId)
                .orElseThrow(() -> new IllegalArgumentException("Node NOT FOUND with ID: " + targetNodeId));

        //  Validacije za metodu, mada verovatno bolje da stavimo ovo u controller
        if (sourceNode.equals(targetNode)) {
            throw new IllegalArgumentException("Source and target nodes cannot be the same");
        }

        if (!sourceNode.getKnowledgeDomain().getId().equals(targetNode.getKnowledgeDomain().getId())) {
            throw new IllegalArgumentException("Source and target nodes must exist in the same knowledge domain");
        }

        boolean linkExists = linkRepository.existsBySourceNodeAndTargetNode(sourceNode, targetNode);

        if (linkExists) {
            throw new IllegalArgumentException("A link already exists between these two nodes");
        }

        KnowledgeDomain knowledgeDomain = knowledgeDomainRepository.findById(sourceNode.getKnowledgeDomain().getId())   //  Kako bismo upisali vezu u prostor
                .orElseThrow(() -> new IllegalArgumentException("Knowledge domain NOT FOUND"));

        link.setSourceNode(sourceNode);
        link.setTargetNode(targetNode);
        link.setKnowledgeDomain(knowledgeDomain);

        knowledgeDomain.getLinksInDomain().add(link);

        linkRepository.save(link);
        knowledgeDomainRepository.save(knowledgeDomain);
        return link;
    }

    public void deleteLink(Long id) {
        linkRepository.deleteById(id);
    }
}
