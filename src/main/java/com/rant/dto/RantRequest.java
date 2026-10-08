package com.rant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RantRequest {
    @NotBlank(message = "Rant body cannot be empty")
    @Size(max = 280, message = "Rant cannot exceed 280 characters")
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
