package com.sots.backend.KnowledgeDomain.Service;

import ch.qos.logback.core.net.SyslogOutputStream;
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
import com.sots.backend.Test.Model.AnsweredQuestion;
import com.sots.backend.Test.Model.Result;
import com.sots.backend.Test.Model.Test;
import com.sots.backend.Test.Repository.ResultRepository;
import com.sots.backend.Test.Repository.TestRepository;
import com.sots.backend.User.Model.User;
import com.sots.backend.User.Repository.UserRepository;
import com.sots.backend.User.Service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class KnowledgeDomainService {

    @Autowired
    private KnowledgeDomainRepository knowledgeDomainRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private KSFlaskService ksFlaskService;
    @Transactional
    public KnowledgeDomainResponse getRealKnowledgeDomain(Long id){
        KnowledgeDomain knowledgeDomain = testRepository.findKnowledgeDomainByTestId(id);
        List<Result> resultList = resultRepository.findAllByTestId(id);
        int[][] matrix = generateMatrix(resultList);

        int[][] result = ksFlaskService.getIITAImplications(matrix).block();

        if (result == null) {
            throw new RuntimeException("IITA call failed!");
        }

        KnowledgeDomain realKnowledgeDomain = generateRealKnowledgeDomainFromImplications(result, resultList);

        if(knowledgeDomainRepository.existsRealKnowledgeDomainByName(knowledgeDomain.getName().concat("_REAL"))){
            KnowledgeDomain existingRealKnowledgeDomain = knowledgeDomainRepository.findByName(realKnowledgeDomain.getName());
            linkRepository.deleteLinksByKnowledgeDomainId(existingRealKnowledgeDomain.getId());
            knowledgeDomainRepository.deleteRealKnowledgeDomainByName(knowledgeDomain.getName().concat("_REAL"));
        }

        knowledgeDomainRepository.save(realKnowledgeDomain);

        return mapKnowledgeDomainToDTO(realKnowledgeDomain);
    }

    private List<Node> getSortedNodesFromResults(List<Result> results){
        Result result = results.get(0);
        List<Node> nodes = new ArrayList<>();
        List<AnsweredQuestion> answeredQuestionList = result.getAnsweredQuestions();
        Collections.sort(answeredQuestionList, Comparator.comparing(AnsweredQuestion::getId));
        for(AnsweredQuestion aq : answeredQuestionList){
            nodes.add(aq.getQuestion().getNode());
        }
        return nodes;
    }

    private KnowledgeDomain generateRealKnowledgeDomainFromImplications(int[][] implications, List<Result> results) {
        List<Node> nodes = getSortedNodesFromResults(results);

        KnowledgeDomain knowledgeDomain = new KnowledgeDomain();
        knowledgeDomain.setNodesInDomain(nodes);
        knowledgeDomain.setReal(true);
        knowledgeDomain.setName(results.get(0).getTest().getKnowledgeDomain().getName().concat("_REAL"));
        knowledgeDomain.setDescription(results.get(0).getTest().getKnowledgeDomain().getDescription().concat(" (Realan prostor znanja)"));
        knowledgeDomain.setCreatedAt(LocalDate.now());
        knowledgeDomain.setProfessor(results.get(0).getTest().getKnowledgeDomain().getProfessor());

        List<Link> links = new ArrayList<>();

        for (int[] implication : implications) {
            int sourceIndex = implication[0];
            int targetIndex = implication[1];

            Node sourceNode = nodes.get(sourceIndex);
            Node targetNode = nodes.get(targetIndex);

            Link link = Link.builder()
                    .label("Implication") // Opcionalni label
                    .sourceNode(sourceNode)
                    .targetNode(targetNode)
                    .knowledgeDomain(knowledgeDomain) // Veza pripada ovom domenu
                    .build();

            links.add(link);
        }

        knowledgeDomain.setLinksInDomain(links);

        return knowledgeDomain;
    }


    private int[][] generateMatrix(List<Result> results) {
        int rows = results.size();
        int cols = results.get(0).getAnsweredQuestions().size();

        int[][] matrix = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            Result result = results.get(i);

            for (int j = 0; j < cols; j++) {
                List<AnsweredQuestion> answeredQuestionList = result.getAnsweredQuestions();
                Collections.sort(answeredQuestionList, Comparator.comparing(AnsweredQuestion::getId));
                AnsweredQuestion answeredQuestion = answeredQuestionList.get(j);

                if (answeredQuestion.getSelectedAnswer() != null && answeredQuestion.getSelectedAnswer().isCorrect()) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = 0;
                }
            }
        }

        return matrix;
    }

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

    public KnowledgeDomainResponse findById(Long id) {
        KnowledgeDomain knowledgeDomain = knowledgeDomainRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Domain not found with id: " + id));

        return mapKnowledgeDomainToDTO(knowledgeDomain);
    }

    public KnowledgeDomain findKDById(Long id) {
        KnowledgeDomain knowledgeDomain = knowledgeDomainRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Domain not found with id: " + id));

        return knowledgeDomain;
    }


    //  Metoda za vezivanje pitanja za cvor
}
