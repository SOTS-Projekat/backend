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
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

        int[][] result = ksFlaskService.getIITAImplications(matrix).block();    //odavde se dobijaju realne implikacije, na osnovu kojih znamo kako su cvorovi zapravo povezani (kako zapravo treba da se uci)

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

        //System.out.println(Arrays.deepToString(matrix));
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

    public KnowledgeDomainResponse save(KnowledgeDomainRequest request) {
        User professor = userRepository.findById(Long.parseLong(request.getProfessorId()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        KnowledgeDomain knowledgeDomain = buildKnowledgeDomain(request, professor);

        KnowledgeDomain savedKnowledgeDomain = knowledgeDomainRepository.save(knowledgeDomain);

        List<Node> nodes = mapNodeRequestsToEntities(request.getNodes(), savedKnowledgeDomain);
        List<Node> savedNodes = nodeRepository.saveAll(nodes);

        List<Link> links = mapLinkRequestsToEntities(request.getLinks(), nodeRepository, savedKnowledgeDomain);
        List<Link> savedLinks = linkRepository.saveAll(links);

        savedKnowledgeDomain.setNodesInDomain(savedNodes);
        savedKnowledgeDomain.setLinksInDomain(savedLinks);

        return mapKnowledgeDomainToDTO(savedKnowledgeDomain);
    }

    @Transactional
    public KnowledgeDomainResponse update(Long id, KnowledgeDomainRequest request) {
        KnowledgeDomain knowledgeDomain = knowledgeDomainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KnowledgeDomain not found with id: " + id));

        knowledgeDomain.setName(request.getName());
        knowledgeDomain.setDescription(request.getDescription());

        KnowledgeDomain updatedKnowledgeDomain = knowledgeDomainRepository.save(knowledgeDomain);
        return mapKnowledgeDomainToDTO(updatedKnowledgeDomain);
    }

    private KnowledgeDomain buildKnowledgeDomain(KnowledgeDomainRequest request, User professor) {
        return KnowledgeDomain.builder()
                .name(request.getName())
                .description(request.getDescription())
                .professor(professor)
                .createdAt(LocalDate.now())
                .build();
    }

    private List<Node> mapNodeRequestsToEntities(List<NodeRequest> nodeRequests, KnowledgeDomain domain) {
        return nodeRequests.stream()
                .map(nr -> Node.builder()
                        .label(nr.getName())
                        .frontendId(nr.getId())
                        .knowledgeDomain(domain)
                        .build())
                .collect(Collectors.toList());
    }

    private List<Link> mapLinkRequestsToEntities(List<LinkRequest> linkRequests, NodeRepository nodeRepository, KnowledgeDomain domain) {
        return linkRequests.stream()
                .map(l -> {
                    Node sourceNode = nodeRepository.findByFrontendId(l.getSource().getId());
                    Node targetNode = nodeRepository.findByFrontendId(l.getTarget().getId());
                    return Link.builder()
                            .label(l.getName())
                            .sourceNode(sourceNode)
                            .targetNode(targetNode)
                            .knowledgeDomain(domain)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private KnowledgeDomainResponse mapKnowledgeDomainToDTO(KnowledgeDomain knowledgeDomain) {
        return KnowledgeDomainResponse.builder()
                .id(knowledgeDomain.getId())
                .professor(mapUserToResponse(knowledgeDomain.getProfessor()))
                .name(knowledgeDomain.getName())
                .description(knowledgeDomain.getDescription())
                .date(knowledgeDomain.getCreatedAt())
                .nodes(mapNodesToResponses(knowledgeDomain.getNodesInDomain()))
                .links(mapLinksToResponses(knowledgeDomain.getLinksInDomain()))
                .build();
    }

    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .id(user.getId())
                .build();
    }

    private List<NodeResponse> mapNodesToResponses(List<Node> nodes) {
        return nodes.stream()
                .map(n -> NodeResponse.builder()
                        .id(n.getId())
                        .frontendId(n.getFrontendId())
                        .label(n.getLabel())
                        .build())
                .collect(Collectors.toList());
    }

    private List<LinkResponse> mapLinksToResponses(List<Link> links) {
        return links.stream()
                .map(l -> LinkResponse.builder()
                        .id(l.getId())
                        .label(l.getLabel())
                        .sourceNode(mapNodeToResponse(l.getSourceNode()))
                        .targetNode(mapNodeToResponse(l.getTargetNode()))
                        .build())
                .collect(Collectors.toList());
    }

    private NodeResponse mapNodeToResponse(Node node) {
        return NodeResponse.builder()
                .id(node.getId())
                .frontendId(node.getFrontendId())
                .label(node.getLabel())
                .build();
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

    public List<NodeResponse> getCorrectStudentAnswers(long testId, long studentId) {
        Result result = resultRepository.findByTestIdAndStudentId(testId, studentId)
                .orElseThrow(() -> new RuntimeException("Result not found with testId: " + testId + " and studentId: " + studentId));

        List<AnsweredQuestion> answeredQuestionList = result.getAnsweredQuestions();
        answeredQuestionList.sort(Comparator.comparing(AnsweredQuestion::getId));

        List<NodeResponse> nodeDTOList = new ArrayList<>();

        for (AnsweredQuestion ansQ : answeredQuestionList) {
            Node node = ansQ.getQuestion().getNode();

            boolean isCorrect = ansQ.getSelectedAnswer() != null && ansQ.getSelectedAnswer().isCorrect();

            nodeDTOList.add(NodeResponse.builder()
                    .id(node.getId())
                    .frontendId(node.getFrontendId())
                    .label(node.getLabel())
                    .correct(isCorrect) //  Mozda ovde samo staviti true, kako bi izbacio tacne cvorove (samo one koji su za bojenje)
                    .build());
        }

        return nodeDTOList;
    }






}
