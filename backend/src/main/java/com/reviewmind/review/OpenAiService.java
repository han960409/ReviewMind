package com.reviewmind.review;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String translateToEnglish(String koreanText) {
        String prompt = """
                Translate the following Korean review into natural English.
                Return only the translated English sentence.
                
                Korean review:
                %s
                """.formatted(koreanText);

        return callOpenAi(prompt);
    }

    public String explainInKorean(
            String originalText,
            String translatedText,
            String sentiment,
            double score
    ) {
        String prompt = """
                다음 감정 분석 결과를 한국어로 2문장 이내로 설명해줘.
                너무 길게 쓰지 말고, 왜 그렇게 판단했는지 자연스럽게 설명해.
                
                원문:
                %s
                
                영어 번역:
                %s
                
                분석 결과:
                %s
                
                확률:
                %.3f
                """.formatted(originalText, translatedText, sentiment, score);

        return callOpenAi(prompt);
    }

    private String callOpenAi(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.2
        );

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, entity, Map.class);

        Map responseBody = response.getBody();

        List choices = (List) responseBody.get("choices");
        Map firstChoice = (Map) choices.get(0);
        Map message = (Map) firstChoice.get("message");

        return message.get("content").toString().trim();
    }
}