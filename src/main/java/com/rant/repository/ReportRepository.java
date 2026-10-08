package com.rant.repository;

import com.rant.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    boolean existsByRantIdAndClientHash(UUID rantId, String clientHash);
}
