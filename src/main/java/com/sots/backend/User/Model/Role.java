package com.sots.backend.User.Model;

public enum Role {
    STUDENT,
    PROFESSOR;

    public String getAuthority() {
        return name(); // Returns "PROFESSOR" or "STUDENT"
    }

}