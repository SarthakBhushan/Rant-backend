package com.rant.repository;

import com.rant.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, UUID> {
    Optional<Reaction> findByRantIdAndClientHash(UUID rantId, String clientHash);
}
