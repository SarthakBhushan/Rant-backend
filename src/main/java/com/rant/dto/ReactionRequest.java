package com.rant.dto;

import com.rant.entity.ReactionType;
import jakarta.validation.constraints.NotNull;

public class ReactionRequest {
    @NotNull(message = "Reaction type is required")
    private ReactionType type;

    public ReactionType getType() {
        return type;
    }

    public void setType(ReactionType type) {
        this.type = type;
    }
}
