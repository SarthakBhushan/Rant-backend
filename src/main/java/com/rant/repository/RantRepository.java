package com.rant.repository;

import com.rant.entity.Rant;
import com.rant.entity.RantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface RantRepository extends JpaRepository<Rant, UUID> {

    Page<Rant> findByStatusAndExpiresAtAfter(RantStatus status, OffsetDateTime now, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Rant r WHERE r.expiresAt < :now")
    int deleteExpiredRants(OffsetDateTime now);

    @Modifying
    @Query("UPDATE Rant r SET r.likeCount = r.likeCount + :d WHERE r.id = :id")
    int adjustLikes(UUID id, int d);

    @Modifying
    @Query("UPDATE Rant r SET r.dislikeCount = r.dislikeCount + :d WHERE r.id = :id")
    int adjustDislikes(UUID id, int d);

    @Modifying
    @Query("UPDATE Rant r SET r.reportCount = r.reportCount + :d WHERE r.id = :id")
    int adjustReports(UUID id, int d);
}
