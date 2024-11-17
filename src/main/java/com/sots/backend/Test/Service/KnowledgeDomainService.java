package com.sots.backend.Test.Service;

import com.sots.backend.Test.Model.KnowledgeDomain;
import com.sots.backend.Test.Model.Link;
import com.sots.backend.Test.Model.Node;
import com.sots.backend.Test.Model.Question;
import com.sots.backend.Test.Repository.KnowledgeDomainRepository;
import com.sots.backend.Test.Repository.LinkRepository;
import com.sots.backend.Test.Repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeDomainService {

    @Autowired
    private KnowledgeDomainRepository knowledgeDomainRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private LinkRepository linkRepository;

    public KnowledgeDomain createEmptyKnowledgeDomain(String name) {
        KnowledgeDomain knowledgeDomain = new KnowledgeDomain();
        knowledgeDomain.setName(name);
        return knowledgeDomainRepository.save(knowledgeDomain);
    }

    public void deleteKnowledgeDomain(Long id) {
        knowledgeDomainRepository.deleteById(id);
    }

    public Node createNode(String label, double x, double y, KnowledgeDomain knowledgeDomain) {
        Node node = new Node();
        node.setLabel(label);
        node.setX(x);
        node.setY(y);
        node.setKnowledgeDomain(knowledgeDomain);
        knowledgeDomain.getNodesInDomain().add(node);
        nodeRepository.save(node);
        knowledgeDomainRepository.save(knowledgeDomain);
        return node;
    }

    public void deleteNode(Long id) {
        nodeRepository.deleteById(id);
    }

    public Link createLink(String nodeFrom, String nodeTo, KnowledgeDomain knowledgeDomain) {
        Link link = new Link();
        link.setSource(nodeFrom);
        link.setTarget(nodeTo);
        link.setKnowledgeDomain(knowledgeDomain);
        knowledgeDomain.getLinksInDomain().add(link);
        linkRepository.save(link);
        knowledgeDomainRepository.save(knowledgeDomain);
        return link;
    }

    public void deleteLink(Long id) {
        linkRepository.deleteById(id);
    }

    public KnowledgeDomain findKnowledgeDomainById(Long knowledgeDomainId) {
        return knowledgeDomainRepository.findById(knowledgeDomainId)
                .orElseThrow(() -> new UsernameNotFoundException("Domain not found"));
    }

    //  Metoda za vezivanje pitanja za cvor
}
