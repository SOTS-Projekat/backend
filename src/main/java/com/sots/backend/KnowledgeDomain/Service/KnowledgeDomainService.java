package com.sots.backend.KnowledgeDomain.Service;

import com.sots.backend.KnowledgeDomain.DTO.Request.KnowledgeDomainRequest;
import com.sots.backend.KnowledgeDomain.DTO.Request.LinkRequest;
import com.sots.backend.KnowledgeDomain.DTO.Request.NodeRequest;
import com.sots.backend.KnowledgeDomain.DTO.Response.KnowledgeDomainResponse;
import com.sots.backend.KnowledgeDomain.DTO.Response.LinkResponse;
import com.sots.backend.KnowledgeDomain.DTO.Response.NodeResponse;
import com.sots.backend.KnowledgeDomain.DTO.Response.UserResponse;
import com.sots.backend.KnowledgeDomain.Model.KnowledgeDomain;
import com.sots.backend.KnowledgeDomain.Model.Link;
import com.sots.backend.KnowledgeDomain.Model.Node;
import com.sots.backend.KnowledgeDomain.Repository.KnowledgeDomainRepository;
import com.sots.backend.KnowledgeDomain.Repository.LinkRepository;
import com.sots.backend.KnowledgeDomain.Repository.NodeRepository;
import com.sots.backend.User.Model.User;
import com.sots.backend.User.Repository.UserRepository;
import com.sots.backend.User.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeDomainService {

    @Autowired
    private KnowledgeDomainRepository knowledgeDomainRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private LinkRepository linkRepository;

//    public KnowledgeDomain createEmptyKnowledgeDomain() {
//
//        if (knowledgeDomainRepository.existsByNameIgnoreCase(name)) {
//            throw new IllegalArgumentException("A domain with this name already exists");
//        }
//
//        User professor = userRepository.getById()
//        if (professor == null || professor.getRole() != Role.PROFESSOR) {
//            throw new IllegalArgumentException("Invalid professor username");
//        }
//
//        KnowledgeDomain knowledgeDomain = new KnowledgeDomain();
//        knowledgeDomain.setName(name);
//        knowledgeDomain.setProfessor(professor);
//
//        return knowledgeDomainRepository.save(knowledgeDomain);
//    }

    public List<KnowledgeDomainResponse> getAll(Long professorId){
        List<KnowledgeDomain> knowledgeDomains = knowledgeDomainRepository.findAllByProfessorId(professorId);
        List<KnowledgeDomainResponse> knowledgeDomainResponses = new ArrayList<>();

        for(KnowledgeDomain kd : knowledgeDomains){
            knowledgeDomainResponses.add(mapKnowledgeDomainToDTO(kd));
        }

        return knowledgeDomainResponses;
    }

    public List<KnowledgeDomainResponse> getAllForTestCreation(Long professorId){
        List<KnowledgeDomain> knowledgeDomains = knowledgeDomainRepository.findAllByProfessorId(professorId);
        List<KnowledgeDomainResponse> knowledgeDomainResponses = new ArrayList<>();

        for(KnowledgeDomain kd : knowledgeDomains){
            knowledgeDomainResponses.add(mapKnowledgeDomainToDTO(kd));
        }

        return knowledgeDomainResponses;
    }

    public KnowledgeDomainResponse save(KnowledgeDomainRequest request){
        User professor = userRepository.findById(Long.parseLong(request.getProfessorId()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        KnowledgeDomain knowledgeDomain = KnowledgeDomain.builder()
                .name(request.getName())
                .description(request.getDescription())
                .professor(professor)
                .createdAt(LocalDate.now())
                .build();

        KnowledgeDomain savedKnowledgeDomain = knowledgeDomainRepository.save(knowledgeDomain);

        List<Node> nodes = new ArrayList<>();
        for(NodeRequest nr : request.getNodes()){
            Node node = Node.builder()
                    .label(nr.getName())
                    .frontendId(nr.getId())
                    .knowledgeDomain(savedKnowledgeDomain)
                    .build();
            nodes.add(node);
        }
        List<Node> savedNodes = nodeRepository.saveAll(nodes);

        List<Link> links = new ArrayList<>();
        for(LinkRequest l : request.getLinks()){
            Node targetNode = nodeRepository.findByFrontendId(l.getTarget().getId());
            Node sourceNode = nodeRepository.findByFrontendId(l.getSource().getId());
            links.add(Link.builder()
                    .label(l.getName())
                    .targetNode(targetNode)
                    .sourceNode(sourceNode)
                    .knowledgeDomain(savedKnowledgeDomain)
                    .build());
        }
        List<Link> savedLinks = linkRepository.saveAll(links);
        savedKnowledgeDomain.setNodesInDomain(nodes);
        savedKnowledgeDomain.setLinksInDomain(links);

        KnowledgeDomainResponse knowledgeDomainResponse = mapKnowledgeDomainToDTO(savedKnowledgeDomain);
        return knowledgeDomainResponse;
    }

    private KnowledgeDomainResponse mapKnowledgeDomainToDTO(KnowledgeDomain knowledgeDomain){
        UserResponse userResponse = UserResponse.builder()
                .email(knowledgeDomain.getProfessor().getEmail())
                .username(knowledgeDomain.getProfessor().getUsername())
                .role(knowledgeDomain.getProfessor().getRole())
                .id(knowledgeDomain.getProfessor().getId())
                .build();
        KnowledgeDomainResponse returnKnowledgeDomain = KnowledgeDomainResponse.builder()
                .id(knowledgeDomain.getId())
                .professor(userResponse)
                .name(knowledgeDomain.getName())
                .description(knowledgeDomain.getDescription())
                .date(knowledgeDomain.getCreatedAt())
                .build();

        List<NodeResponse> nodeResponses = new ArrayList<>();
        for(Node n : knowledgeDomain.getNodesInDomain()){
            NodeResponse nodeResponse = NodeResponse.builder()
                    .id(n.getId())
                    .frontendId(n.getFrontendId())
                    .label(n.getLabel())
                    .build();
            nodeResponses.add(nodeResponse);
        }

        List<LinkResponse> linkResponses = new ArrayList<>();
        for(Link l : knowledgeDomain.getLinksInDomain()){
            LinkResponse linkResponse = LinkResponse.builder()
                    .id(l.getId())
                    .label(l.getLabel())
                    .sourceNode(NodeResponse.builder()
                            .id(l.getSourceNode().getId())
                            .frontendId(l.getSourceNode().getFrontendId())
                            .label(l.getSourceNode().getLabel())
                            .build())
                    .targetNode(NodeResponse.builder()
                            .id(l.getTargetNode().getId())
                            .frontendId(l.getTargetNode().getFrontendId())
                            .label(l.getTargetNode().getLabel())
                            .build())
                    .build();
            linkResponses.add(linkResponse);
        }

        returnKnowledgeDomain.setNodes(nodeResponses);
        returnKnowledgeDomain.setLinks(linkResponses);

        return returnKnowledgeDomain;
    }

    public void deleteKnowledgeDomain(Long id) {
        knowledgeDomainRepository.deleteById(id);
    }



    //  Metoda za vezivanje pitanja za cvor
}
