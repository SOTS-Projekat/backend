package com.sots.backend.KnowledgeDomain.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sots.backend.KnowledgeDomain.Model.Node;
import com.sots.backend.User.Model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    @OneToMany(mappedBy = "knowledgeDomain", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Sprečava beskonačno ugnježđavanje
    private List<Node> nodesInDomain;

    @OneToMany(mappedBy = "knowledgeDomain", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Sprečava beskonačno ugnježđavanje
    private List<Link> linksInDomain;
}
