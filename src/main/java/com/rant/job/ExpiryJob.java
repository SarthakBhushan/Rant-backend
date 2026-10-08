package com.rant.job;

import com.rant.repository.RantRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

@Component
public class ExpiryJob {

    private static final Logger logger = LoggerFactory.getLogger(ExpiryJob.class);

    private final RantRepository rantRepository;

    public ExpiryJob(RantRepository rantRepository) {
        this.rantRepository = rantRepository;
    }

    @Scheduled(fixedRateString = "${rant.expiry.job.rate:60000}") // Default 1 minute
    @Transactional
    public void deleteExpiredRants() {
        int deletedCount = rantRepository.deleteExpiredRants(OffsetDateTime.now());
        if (deletedCount > 0) {
            logger.info("Deleted {} expired rants", deletedCount);
        }
    }
}
