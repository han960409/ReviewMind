package com.reviewmind.review;

import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api-key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();

    public String explain(String originalText, String sentiment, double score) {
        String prompt = """
                다음 리뷰 감정분석 결과를 한국어로 2문장 이내로 설명해줘.

                리뷰:
                %s

                감정분석 결과:
                %s

                확률:
                %.3f
                """.formatted(originalText, sentiment, score);

        return callGemini(prompt);
    }

    private String callGemini(String prompt) {
        try {
            JSONObject textPart = new JSONObject()
                    .put("text", prompt);

            JSONArray parts = new JSONArray()
                    .put(textPart);

            JSONObject content = new JSONObject()
                    .put("parts", parts);

            JSONArray contents = new JSONArray()
                    .put(content);

            JSONObject bodyJson = new JSONObject()
                    .put("contents", contents);

            RequestBody body = RequestBody.create(
                    bodyJson.toString(),
                    MediaType.parse("application/json")
            );

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key="
                            + apiKey;

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (!response.isSuccessful()) {
                    throw new RuntimeException("Gemini API 오류: " + response);
                }

                String responseBody = response.body().string();

                JSONObject json = new JSONObject(responseBody);

                return json
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                        .trim();
            }
        } catch (Exception e) {
            throw new RuntimeException("Gemini 설명 생성 실패", e);
        }
    }

    public String translateToEnglish(String koreanText) {
        String prompt = """
                Translate the following Korean movie review into natural English.
                Return only the English translation.

                Korean review:
                %s
                """.formatted(koreanText);

        return callGemini(prompt);
        }
}