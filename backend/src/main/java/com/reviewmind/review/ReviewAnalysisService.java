package com.reviewmind.review;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewAnalysisService {

    private final ReviewAnalysisRepository repository;
    private final GeminiService geminiService;

    @Value("${fastapi.url}")
    private String fastApiUrl;

    public ReviewAnalyzeResponse analyze(ReviewAnalyzeRequest request) {

        String originalText = request.getContent();

        String translatedText = geminiService.translateToEnglish(originalText);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "text",
                translatedText
        );

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<FastApiSentimentResponse> response =
                restTemplate.postForEntity(
                        fastApiUrl + "/predict",
                        entity,
                        FastApiSentimentResponse.class
                );

        FastApiSentimentResponse result = response.getBody();

        if (result == null) {
            throw new RuntimeException("FastAPI 응답이 비어 있습니다.");
        }

        String explanation = geminiService.explain(
                originalText,
                result.getLabel(),
                result.getScore()
        );

        ReviewAnalysis saved = repository.save(
                new ReviewAnalysis(
                        originalText,
                        translatedText,
                        result.getLabel(),
                        result.getScore(),
                        explanation
                )
        );

        return new ReviewAnalyzeResponse(
                saved.getId(),
                saved.getOriginalText(),
                saved.getTranslatedText(),
                saved.getSentiment(),
                saved.getScore(),
                saved.getExplanation()
        );
    }
}