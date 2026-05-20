package com.reviewmind.review;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewAnalysisRepository
        extends JpaRepository<ReviewAnalysis, Long> {
}