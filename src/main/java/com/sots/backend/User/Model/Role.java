package com.sots.backend.User.Model;

public enum Role {
    STUDENT,
    PROFESSOR,
    ADMINISTRATOR;

    public String getAuthority() {
        return name(); // Returns "PROFESSOR" or "STUDENT"
    }

}