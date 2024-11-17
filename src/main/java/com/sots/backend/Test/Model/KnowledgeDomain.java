package com.sots.backend.Test.Model;

import com.sots.backend.User.Model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToMany(mappedBy = "knowledgeDomain", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Node> nodesInDomain;

    @OneToMany(mappedBy = "knowledgeDomain", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Link> linksInDomain;



//    public KnowledgeDomain(Long id, String name) {
//        this.id = id;
//        this.name = name;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getNodesInDomain() {
        return nodesInDomain;
    }

    public List<Link> getLinksInDomain() {
        return linksInDomain;
    }
}
