package com.rant.service;

import com.rant.dto.RantCreationResponse;
import com.rant.dto.RantResponse;
import com.rant.entity.*;
import com.rant.repository.RantRepository;
import com.rant.repository.ReactionRepository;
import com.rant.repository.ReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RantService {

    private final RantRepository rantRepository;
    private final ReactionRepository reactionRepository;
    private final ReportRepository reportRepository;
    private final ContentFilterService contentFilterService;

    private static final int AUTO_HIDE_REPORT_THRESHOLD = 5;

    private static String sha256(String s) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }


    public RantService(RantRepository rantRepository, ReactionRepository reactionRepository, 
                       ReportRepository reportRepository, ContentFilterService contentFilterService) {
        this.rantRepository = rantRepository;
        this.reactionRepository = reactionRepository;
        this.reportRepository = reportRepository;
        this.contentFilterService = contentFilterService;
    }

    @Transactional
    public RantCreationResponse createRant(String body) {
        String filteredBody = contentFilterService.filterAndMask(body);

        String deleteToken = UUID.randomUUID().toString();
        String deleteTokenHash = sha256(deleteToken);

        Rant rant = new Rant();
        rant.setBody(filteredBody);
        rant.setDeleteTokenHash(deleteTokenHash);
        
        rant = rantRepository.save(rant);
        return new RantCreationResponse(rant.getId(), deleteToken);
    }

    @org.springframework.cache.annotation.Cacheable(value = "feedCache", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<RantResponse> getFeed(Pageable pageable) {
        return rantRepository.findByStatusAndExpiresAtAfter(
                RantStatus.ACTIVE, OffsetDateTime.now(), pageable
        ).map(r -> new RantResponse(r.getId(), r.getBody(), r.getLikeCount(), r.getDislikeCount(), r.getExpiresAt()));
    }

    @Transactional
    public void reactToRant(UUID rantId, String clientHash, ReactionType type) {
        Rant rant = rantRepository.findById(rantId)
                .orElseThrow(() -> new IllegalArgumentException("Rant not found"));

        if (rant.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalStateException("Cannot react to an expired rant");
        }

        Optional<Reaction> existingOpt = reactionRepository.findByRantIdAndClientHash(rantId, clientHash);
        if (existingOpt.isPresent()) {
            Reaction existing = existingOpt.get();
            if (existing.getType() == type) {
                return; // Already reacted with same type
            }
            // Swap reaction
            adjust(rantId, existing.getType(), -1);
            existing.setType(type);
        } else {
            // New reaction
            Reaction reaction = new Reaction();
            reaction.setRant(rant);
            reaction.setClientHash(clientHash);
            reaction.setType(type);
            reactionRepository.save(reaction);
        }

        adjust(rantId, type, +1);
    }

    @Transactional
    public void removeReaction(UUID rantId, String clientHash) {
        Rant rant = rantRepository.findById(rantId)
                .orElseThrow(() -> new IllegalArgumentException("Rant not found"));

        reactionRepository.findByRantIdAndClientHash(rantId, clientHash).ifPresent(reaction -> {
            adjust(rantId, reaction.getType(), -1);
            reactionRepository.delete(reaction);
        });
    }

    @Transactional
    public void reportRant(UUID rantId, String clientHash) {
        Rant rant = rantRepository.findById(rantId)
                .orElseThrow(() -> new IllegalArgumentException("Rant not found"));

        if (reportRepository.existsByRantIdAndClientHash(rantId, clientHash)) {
            return; // Already reported
        }

        Report report = new Report();
        report.setRant(rant);
        report.setClientHash(clientHash);
        reportRepository.save(report);

        rantRepository.adjustReports(rantId, 1);
        if(rant.getReportCount()+1>= AUTO_HIDE_REPORT_THRESHOLD){
            rant.setStatus(RantStatus.HIDDEN);
            rantRepository.save(rant);
        }
    }

    @Transactional
    public void deleteRant(UUID rantId, String deleteToken) {
        Rant rant = rantRepository.findById(rantId)
                .orElseThrow(() -> new IllegalArgumentException("Rant not found"));

        String hash = sha256(deleteToken);
        if (!rant.getDeleteTokenHash().equals(hash)) {
            throw new IllegalArgumentException("Invalid delete token");
        }

        rantRepository.delete(rant);
    }

    private void adjust(UUID id, ReactionType t, int d) {
        if (t == ReactionType.LIKE) rantRepository.adjustLikes(id, d);
        else rantRepository.adjustDislikes(id, d);
    }
}
