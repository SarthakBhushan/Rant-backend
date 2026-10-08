package com.rant.controller;

import com.rant.dto.RantCreationResponse;
import com.rant.dto.RantRequest;
import com.rant.dto.RantResponse;
import com.rant.dto.ReactionRequest;
import com.rant.service.RantService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rants")
public class RantController {

    private final RantService rantService;

    public RantController(RantService rantService) {
        this.rantService = rantService;
    }

    @PostMapping
    public ResponseEntity<RantCreationResponse> createRant(@Valid @RequestBody RantRequest request) {
        RantCreationResponse response = rantService.createRant(request.getBody());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<RantResponse>> getFeed(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC, size = 20) Pageable pageable) {
        return ResponseEntity.ok(rantService.getFeed(pageable));
    }

    @PutMapping("/{id}/reaction")
    public ResponseEntity<Void> reactToRant(
            @PathVariable UUID id,
            @RequestHeader("X-Client-Hash") String clientHash,
            @Valid @RequestBody ReactionRequest request) {
        rantService.reactToRant(id, clientHash, request.getType());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/reaction")
    public ResponseEntity<Void> removeReaction(
            @PathVariable UUID id,
            @RequestHeader("X-Client-Hash") String clientHash) {
        rantService.removeReaction(id, clientHash);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<Void> reportRant(
            @PathVariable UUID id,
            @RequestHeader("X-Client-Hash") String clientHash) {
        rantService.reportRant(id, clientHash);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRant(
            @PathVariable UUID id,
            @RequestHeader("X-Delete-Token") String deleteToken) {
        rantService.deleteRant(id, deleteToken);
        return ResponseEntity.noContent().build();
    }
}
