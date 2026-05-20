package com.reviewmind.review;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FastApiSentimentResponse {

    private String label;
    private double score;
}