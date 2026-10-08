package com.rant.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "rants")
public class Rant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 280)
    private String body;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int dislikeCount = 0;

    @Column(nullable = false)
    private int reportCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RantStatus status = RantStatus.ACTIVE;

    @Column(nullable = false)
    private String deleteTokenHash;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.expiresAt = this.createdAt.plusHours(24);
    }

    // Getters and Setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getDislikeCount() { return dislikeCount; }
    public void setDislikeCount(int dislikeCount) { this.dislikeCount = dislikeCount; }

    public int getReportCount() { return reportCount; }
    public void setReportCount(int reportCount) { this.reportCount = reportCount; }

    public RantStatus getStatus() { return status; }
    public void setStatus(RantStatus status) { this.status = status; }

    public String getDeleteTokenHash() { return deleteTokenHash; }
    public void setDeleteTokenHash(String deleteTokenHash) { this.deleteTokenHash = deleteTokenHash; }
}
