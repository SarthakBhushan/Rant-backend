package com.rant.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "reactions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"rant_id", "client_hash"})
})
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Rant rant;

    @Column(name = "client_hash", nullable = false)
    private String clientHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    // Getters and Setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Rant getRant() { return rant; }
    public void setRant(Rant rant) { this.rant = rant; }

    public String getClientHash() { return clientHash; }
    public void setClientHash(String clientHash) { this.clientHash = clientHash; }

    public ReactionType getType() { return type; }
    public void setType(ReactionType type) { this.type = type; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
