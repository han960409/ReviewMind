package com.reviewmind.review;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewAnalyzeResponse {

    private Long id;
    private String originalText;
    private String translatedText;
    private String sentiment;
    private double score;
    private String explanation;
}