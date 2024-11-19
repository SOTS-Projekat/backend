package com.sots.backend.KnowledgeDomain.DTO;

public class NodeRequest {
    private String label;
    private double x;
    private double y;
    private Long knowledgeDomainId;
    //private List<Long> questionIds;

    public String getLabel() {
        return label;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Long getKnowledgeDomainId() {
        return knowledgeDomainId;
    }
}
