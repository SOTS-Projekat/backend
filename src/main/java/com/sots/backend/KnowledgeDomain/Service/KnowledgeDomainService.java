package com.sots.backend.KnowledgeDomain.Service;

import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import com.sots.backend.KnowledgeDomain.Model.Link;
import com.sots.backend.KnowledgeDomain.Model.Node;
import com.sots.backend.KnowledgeDomain.Repository.KnowledgeDomainRepository;
import com.sots.backend.KnowledgeDomain.Repository.LinkRepository;
import com.sots.backend.KnowledgeDomain.Repository.NodeRepository;
import com.sots.backend.User.Model.Role;
import com.sots.backend.User.Model.User;
import com.sots.backend.User.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeDomainService {

    @Autowired
    private KnowledgeDomainRepository knowledgeDomainRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private UserService userService;

    public KnowledgeDomain createEmptyKnowledgeDomain(String name, String professorUsername) {

        if (knowledgeDomainRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("A domain with this name already exists");
        }

        User professor = userService.getProfessorByUsername(professorUsername);
        if (professor == null || professor.getRole() != Role.PROFESSOR) {
            throw new IllegalArgumentException("Invalid professor username");
        }

        KnowledgeDomain knowledgeDomain = new KnowledgeDomain();
        knowledgeDomain.setName(name);
        knowledgeDomain.setProfessor(professor);

        return knowledgeDomainRepository.save(knowledgeDomain);
    }

    public void deleteKnowledgeDomain(Long id) {
        knowledgeDomainRepository.deleteById(id);
    }



    //  Metoda za vezivanje pitanja za cvor
}
