package com.rant.dto;

import java.util.UUID;

public class RantCreationResponse {
    private UUID id;
    private String deleteToken;

    public RantCreationResponse(UUID id, String deleteToken) {
        this.id = id;
        this.deleteToken = deleteToken;
    }

    public UUID getId() { return id; }
    public String getDeleteToken() { return deleteToken; }
}
