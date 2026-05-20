package com.reviewmind.review;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class ReviewAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String originalText;

    @Column(columnDefinition = "TEXT")
    private String translatedText;

    private String sentiment;

    private double score;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private LocalDateTime createdAt;

    public ReviewAnalysis(
            String originalText,
            String translatedText,
            String sentiment,
            double score,
            String explanation
    ) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.sentiment = sentiment;
        this.score = score;
        this.explanation = explanation;
        this.createdAt = LocalDateTime.now();
    }
}