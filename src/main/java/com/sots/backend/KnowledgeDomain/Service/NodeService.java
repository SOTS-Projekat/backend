package com.sots.backend.KnowledgeDomain.Service;

import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import com.sots.backend.KnowledgeDomain.Model.Node;
import com.sots.backend.KnowledgeDomain.Repository.KnowledgeDomainRepository;
import com.sots.backend.KnowledgeDomain.Repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeService {

    @Autowired
    NodeRepository nodeRepository;

    @Autowired
    KnowledgeDomainRepository knowledgeDomainRepository;

    public Node getById(Long id){
        return nodeRepository.getById(id);
    }

//    public Node createNode(String label, double x, double y, Long knowledgeDomainId) {
//
//        if (nodeRepository.existsByLabelIgnoreCase(label)) {
//            throw new IllegalArgumentException("A node with this label already exists");
//        }
//
//        Node node = new Node();
//        node.setLabel(label);
//        node.setX(x);
//        node.setY(y);
//
//        KnowledgeDomain knowledgeDomain = knowledgeDomainRepository.findById(knowledgeDomainId)
//                .orElseThrow(() -> new IllegalArgumentException("Knowledge domain NOT FOUND with ID: " + knowledgeDomainId));
//
//        node.setKnowledgeDomain(knowledgeDomain);
//        knowledgeDomain.getNodesInDomain().add(node);
//
//        nodeRepository.save(node);
//        knowledgeDomainRepository.save(knowledgeDomain);
//        return node;
//    }

    public void deleteNode(Long id) {
        nodeRepository.deleteById(id);
    }
}
