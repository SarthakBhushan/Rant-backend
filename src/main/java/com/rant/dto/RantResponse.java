package com.rant.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public class RantResponse {
    private UUID id;
    private String body;
    private int likeCount;
    private int dislikeCount;
    private OffsetDateTime expiresAt;

    public RantResponse(UUID id, String body, int likeCount, int dislikeCount, OffsetDateTime expiresAt) {
        this.id = id;
        this.body = body;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.expiresAt = expiresAt;
    }

    // Getters
    public UUID getId() { return id; }
    public String getBody() { return body; }
    public int getLikeCount() { return likeCount; }
    public int getDislikeCount() { return dislikeCount; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
}
